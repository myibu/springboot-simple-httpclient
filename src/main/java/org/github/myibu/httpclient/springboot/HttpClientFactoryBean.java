package org.github.myibu.httpclient.springboot;

import com.github.myibu.httpclient.DefaultHttpClient;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author myibu
 * @since 1.0
 */
public class HttpClientFactoryBean<T> implements FactoryBean<T> {

    private Class<T> clz;

    public HttpClientFactoryBean(Class<T> clz) {
        this.clz = clz;
    }

    @Override
    public T getObject() throws Exception {
        return DefaultHttpClient.newInstance(clz);
    }

    @Override
    public Class<?> getObjectType() {
        return clz;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
