package com.utils.wechat;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * 信任管理器
 * 
 * @author yulin
 */
public class BookStoreX509TrustManager implements X509TrustManager
{
    /*
     * 检查客户端证书
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[],
     * java.lang.String)
     */
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException
    {}

    /*
     * 检查服务器端证书
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[],
     * java.lang.String)
     */
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException
    {}

    /*
     * 返回受信任的X509证书数组
     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    @Override
    public X509Certificate[] getAcceptedIssuers()
    {
        return null;
    }
}
