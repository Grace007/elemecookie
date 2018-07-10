package com.bds.elemecookie.utils;

import com.bds.base.http.CustomGetMethod;
import com.bds.base.http.CustomPostMethod;
import com.bds.base.http.HttpBase;
import com.bds.base.http.Response;
import com.bds.base.util.Constant;
import com.bds.base.util.ProxyIp;
import com.bds.base.util.ProxyPool;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lex on 2017/6/1.
 */
public class ElemeHttpBase {
    private static Logger logger = Logger.getLogger(HttpBase.class);
    private static final ElemeHttpBase instance = new ElemeHttpBase();

    // private static FirefoxProfile profile = new FirefoxProfile();
    private static DesiredCapabilities chrome_capability = DesiredCapabilities.chrome();
    // private static DesiredCapabilities firefox_capability =
    // DesiredCapabilities.firefox();

    private static DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();

    static {
        String dir = System.getProperty("user.dir");

        System.setProperty("webdriver.chrome.driver", dir + "/conf/chromedriver.exe");

        String sys = System.getProperty("os.arch");
        if (sys.indexOf("64") >= 0) {
            System.setProperty("webdriver.ie.driver", dir + "/conf/IEDriverServer64.exe");
            System.setProperty("webdriver.gecko.driver", dir + "/conf/geckodriver64.exe");
        } else {
            System.setProperty("webdriver.ie.driver", dir + "/conf/IEDriverServer32.exe");
            System.setProperty("webdriver.gecko.driver", dir + "/conf/geckodriver32.exe");
        }

        // if (!Constant.DOWN_IMG) {
        // ffProfile.setPreference("permissions.default.image", 2);
        // } else {
        // System.out.println("===============当前需要下载图片=============");
        // }
        ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);

        // 禁用浏览器缓存

        chrome_capability.setJavascriptEnabled(true);
        // firefox_capability.setJavascriptEnabled(true);
        ieCapabilities.setJavascriptEnabled(true);
    }

    private ElemeHttpBase() {
    }

    public static ElemeHttpBase getInstance() {
        return instance;
    }

    private static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();

    static {
        Integer MAX_TIME_OUT = 5000;
        Integer MAX_CONN = 5000;
        connectionManager.closeIdleConnections(MAX_TIME_OUT);
        connectionManager.getParams().setParameter("http.connection-manager.max-total", MAX_CONN);
    }

    public static Response get(String url, String charset) throws Exception {
        return get(url, charset, null);
    }

    /**
     * 默认请求
     *
     * @return
     * @throws Exception
     */
    public synchronized static Response get(String url, String charset, Map<String, String> headers) throws Exception {
        HttpClient client = getClient(charset);
        CustomGetMethod getMethod = new CustomGetMethod(url);// 创建一个get方法，类似在浏览器地址栏中输入一个地址
        if (headers != null) {
            Set<String> keys = headers.keySet();
            for (String k : keys) {
                getMethod.setRequestHeader(k, headers.get(k));
            }
        }
        getMethod.setRequestHeader("accept",
                "*/*");
        getMethod.setRequestHeader("Accept-Encoding", "gzip, deflate, br");
        getMethod.setRequestHeader("Accept-Language", "zh-CN,zh;q=0.8");
        //getMethod.setRequestHeader("Upgrade-Insecure-Requests", "1");
        getMethod.setRequestHeader("User-Agent",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
        if (Constant.proxy) {
            getMethod.setRequestHeader("Proxy-Authorization", Constant.ProxySecret);
        }
        int statusCode = client.executeMethod(getMethod);// 回车——出拳！

        Header[] _map = getMethod.getResponseHeaders();
        Map<String, String> map = new HashMap<String, String>();
        for (Header h : _map) {
            map.put(h.getName(), h.getValue());
        }
        if (statusCode == 200) {
            String body = getMethod.getResponseBodyAsString();
            getMethod.releaseConnection();
            Response r = new Response();
            r.setResult(body);
            r.setHeader(map);
            r.setStatus(statusCode);
            getMethod.abort();
            getMethod.releaseConnection();// 释放，记得收拳哦
            return r;
        }
        if (statusCode == 500) {
            String body = getMethod.getResponseBodyAsString();
            getMethod.releaseConnection();
            if (StringUtils.isNotEmpty(body)) {
                Response r = new Response();
                r.setResult(body);
                r.setHeader(map);
                r.setStatus(statusCode);
                getMethod.abort();
                getMethod.releaseConnection();// 释放，记得收拳哦
                return r;
            }
            getMethod.abort();
            getMethod.releaseConnection();// 释放，记得收拳哦
            throw new ConnectException("请求错误,status code is[" + statusCode + "]");
        }
        String body = getMethod.getResponseBodyAsString();
        Response r = new Response();
        r.setResult(body);
        r.setHeader(map);
        r.setStatus(statusCode);
        getMethod.abort();
        getMethod.releaseConnection();// 释放，记得收拳哦
        return r;
    }

    public synchronized static Response post(String url, String charset, Map<String, String> header,
                                             Map<String, String> data) throws Exception {
        CustomPostMethod postMethod = new CustomPostMethod(url);
        try {
            HttpClient client = getClient(charset);

            if (header != null && !header.isEmpty()) {
                Set<String> headers = header.keySet();
                for (String key : headers) {
                    postMethod.setRequestHeader(key, header.get(key));
                }
            }

            if (Constant.proxy) {
                postMethod.setRequestHeader("Proxy-Authorization", Constant.ProxySecret);
            }
            if (data != null && !data.isEmpty()) {
                Set<String> keys = data.keySet();
                NameValuePair[] ps = new NameValuePair[keys.size()];
                int index = 0;
                for (String k : keys) {
                    ps[index++] = new NameValuePair(k, data.get(k));
                }

                postMethod.setRequestBody(ps);
            }

            int statusCode = client.executeMethod(postMethod);// 回车——出拳！

            Header[] _map = postMethod.getResponseHeaders();

            Map<String, String> map = new HashMap<String, String>();
            for (Header h : _map) {
                map.put(h.getName(), h.getValue());
            }
            if (statusCode == 200) {
                String result = postMethod.getResponseBodyAsString();

                Response r = new Response();
                r.setResult(result);
                r.setHeader(map);
                r.setStatus(statusCode);
                postMethod.abort();
                return r;
            } else if (statusCode == 302) {
                String result = postMethod.getResponseBodyAsString();

                Response r = new Response();
                r.setResult(result);
                r.setHeader(map);
                r.setStatus(statusCode);
                postMethod.abort();
                return r;
            }
            postMethod.abort();
            throw new ConnectException("请求错误,status code is[" + statusCode + "]");
        } catch (Exception e) {
            postMethod.abort();
            throw e;
        } finally {

            postMethod.releaseConnection();
        }

    }

    public synchronized static String post(String url, Map<String, String> header, Map<String, String> params)
            throws ConnectException {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "application/json, text/javascript, */*; q=0.01");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_2 like Mac OS X) AppleWebKit/603.2.4 (KHTML, like Gecko) Mobile/14F89 MicroMessenger/6.5.8 NetType/WIFI Language/zh_CN");
            if (header != null && !header.isEmpty()) {
                Set<String> headers = header.keySet();
                for (String key : headers) {
                    conn.setRequestProperty(key, header.get(key));
                }
            }

            /**
             * 参数
             */
            StringBuffer p = new StringBuffer();
            if (params != null && !params.isEmpty()) {
                Set<String> param = params.keySet();
                for (String k : param) {
                    p.append(k + "=" + params.get(k) + "&");
                }
            }

            if (p.length() > 0) {
                p = p.deleteCharAt(p.length() - 1);
            }

            // 发送POST请求必须设置如下两行
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(p.toString());
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            throw new ConnectException("请求错误,status code is[" + result + "]");
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    private static HttpClient getClient(String charset) {
        HttpClient client = new HttpClient(connectionManager);
        if (Constant.proxy) {
            // ProxyIp ip = new ProxyIp("120.24.184.166", 16816,
            // "20160323174944");
            ProxyIp ip = ProxyPool.getRandomProxy();
            client.getHostConfiguration().setProxy(ip.getIp(), ip.getPort());

            logger.info("########代理ip:" + ip.getIp() + ",端口:" + ip.getPort() + "注册时间：" + ip.getTime() + "########");
        }
        HttpConnectionManagerParams params = client.getHttpConnectionManager().getParams();
        params.setConnectionTimeout(30000);
        params.setSoTimeout(30000);
        params.setDefaultMaxConnectionsPerHost(500);
        params.setMaxTotalConnections(1500);
        params.setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        HttpClientParams hcp = client.getParams();
        hcp.setParameter("http.connection.timeout", 20 * 1000L);
        hcp.setParameter("http.connection-manager.timeout", 20 * 1000L);
        hcp.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
        hcp.setAuthenticationPreemptive(true);
        return client;
    }

    public synchronized static WebDriver getFirefoxDriver() {
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        FirefoxProfile ffProfile = new FirefoxProfile();

        ffProfile.setPreference("network.http.use-cache", false);
        ffProfile.setPreference("browser.cache.memory.enable", false);
        ffProfile.setPreference("browser.cache.disk.enable", false);
        ffProfile.setPreference("browser.sessionhistory.max_total_viewers", 3);
        ffProfile.setPreference("network.dns.disableIPv6", true);
        ffProfile.setPreference("Content.notify.interval", 750000);
        ffProfile.setPreference("content.notify.backoffcount", 3);

        // 有的网站支持 有的不支持
        ffProfile.setPreference("network.http.pipelining", true);
        ffProfile.setPreference("network.http.proxy.pipelining", true);
        ffProfile.setPreference("network.http.pipelining.maxrequests", 32);
        if (Constant.proxy) {
            System.out.println("start firefox browser...");
            ProxyIp ip = ProxyPool.getRandomProxy();
            // 设置代理参数
            // 20.198.231.23:8086
            Proxy proxy = new Proxy();
            proxy.setHttpProxy(ip.getIp() + ":" + ip.getPort()); // 此ip地址是动态的ip地址
            // proxy.setHttpProxy("123.96.182.61:8088"); // 此ip地址是动态的ip地址
            // profile.setPreference("network.proxy.type", 1);
            // profile.setPreference("network.proxy.http", ip.getIp());
            // profile.setPreference("network.proxy.http_port", ip.getPort());
            System.out.println("########代理ip:" + ip.getIp() + ",端口:" + ip.getPort() + "注册时间：" + "########");
            capabilities.setCapability(CapabilityType.PROXY, proxy);

            // ffProfile.s
            // logger.info("########代理ip:" + ip.getIp() + ",端口:" + ip.getPort()
            // + "注册时间：" + ip.getTime() + "########");

        }
        capabilities.setCapability(FirefoxDriver.PROFILE, ffProfile);
        WebDriver driver = new FirefoxDriver(capabilities);

        return driver;

    }

    public synchronized static WebDriver getChromeDriver() {
        if (Constant.proxy) {
            ProxyIp ip = ProxyPool.getRandomProxy();
            Proxy proxy = new Proxy();
            proxy.setHttpProxy(ip.getIp() + ":" + ip.getPort()); // 此ip地址是动态的ip地址
            logger.info("########代理ip:" + ip.getIp() + ",端口:" + ip.getPort() + "########");
            chrome_capability.setCapability(CapabilityType.PROXY, proxy);
        }
        WebDriver driver = new ChromeDriver(chrome_capability);
        return driver;

    }

    public synchronized static WebDriver getIEDriver() {
        if (Constant.proxy) {
            ProxyIp ip = ProxyPool.getRandomProxy();
            Proxy proxy = new Proxy();
            proxy.setHttpProxy(ip.getIp() + ":" + ip.getPort()); // 此ip地址是动态的ip地址
            logger.info("########代理ip:" + ip.getIp() + ",端口:" + ip.getPort() + "########");
            ieCapabilities.setCapability(CapabilityType.PROXY, proxy);
        }

        WebDriver driver = new InternetExplorerDriver(ieCapabilities);
        return driver;

    }

    public static void main(String[] args) {
        Constant.proxy = true;
        try {
            Response r = HttpBase.get("http://blog.csdn.net/tanzhangwen/article/details/16337969", "utf-8");
            System.out.println(r.getResult());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//		try {
//			WebDriver driver = HttpBase.getFirefoxDriver();
//			driver.get("http://blog.csdn.net/tanzhangwen/article/details/16337969");
//			String page = driver.getPageSource();
//			System.out.println(page);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

    }
}
