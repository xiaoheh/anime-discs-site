package fands.support.runner;

import fands.dao.Dao;
import fands.model.ProxyHost;
import org.apache.logging.log4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class ProxyService {

    private Logger logger = LogManager.getLogger(ProxyService.class);
    private Set<ProxyHost> proxys = Collections.synchronizedSet(new HashSet<>());
    private Set<ProxyHost> errors = Collections.synchronizedSet(new HashSet<>());
    private Dao dao;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    @PostConstruct
    public void initProxys() {
        dao.findAll(ProxyHost.class).forEach(this::addProxyHost);
    }

    public synchronized void addProxyHost(ProxyHost proxyHost) {
        if (errors.contains(proxyHost) || proxys.contains(proxyHost)) {
            return;
        }
        if (isErrorHost(proxyHost)) {
            errors.add(proxyHost);
        } else {
            proxys.add(proxyHost);
        }
        logger.printf(Level.INFO, "成功添加代理服务器: %s:%d",
                proxyHost.getHost(), proxyHost.getPort());
    }

    private boolean isErrorHost(ProxyHost proxyHost) {
        if (proxyHost.isBaned()) {
            return true;
        }
        if (proxyHost.getError() > 5) {
            if (proxyHost.getRight() < proxyHost.getError() / 5) {
                return true;
            }
        }
        return false;
    }

    public synchronized void updateProxyHost() {
        ArrayList<ProxyHost> proxyHosts = new ArrayList<>();
        proxyHosts.addAll(proxys);
        proxyHosts.addAll(errors);
        proxyHosts.forEach(dao::saveOrUpdate);
    }

    public synchronized ProxyHost getProxyHost() {
        ArrayList<ProxyHost> proxyHosts = new ArrayList<>(proxys);
        proxyHosts.removeIf(ph -> !isTimeout(ph.getDate()));
        if (proxyHosts.isEmpty()) {
            return null;
        }
        ProxyHost proxyHost = proxyHosts.get(new Random().nextInt(proxyHosts.size()));
        if (isErrorHost(proxyHost)) {
            proxys.remove(proxyHost);
            errors.add(proxyHost);
            return getProxyHost();
        }
        return proxyHost;
    }

    private boolean isTimeout(Date date) {
        return date == null || date.getTime() + 15000 < System.currentTimeMillis();
    }

    public Set<ProxyHost> getProxys() {
        return proxys;
    }

    public Set<ProxyHost> getErrors() {
        return errors;
    }
}
