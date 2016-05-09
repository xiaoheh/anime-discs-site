package fands.support.runner;

import fands.dao.Dao;
import fands.model.ProxyHost;
import org.apache.logging.log4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

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
        List<ProxyHost> errorList = proxys.stream()
                .filter(this::isErrorHost)
                .collect(Collectors.toList());
        proxys.removeAll(errorList);
        errors.addAll(errorList);
        proxys.forEach(dao::saveOrUpdate);
        errors.forEach(dao::saveOrUpdate);
    }

    public synchronized ProxyHost getProxyHost(int errorCount) {
        List<ProxyHost> list = proxys.stream()
                .filter(ph -> isTimeout(ph.getDate()))
                .filter(ph -> !isErrorHost(ph))
                .sorted((o1, o2) -> o2.getRight() - o1.getRight())
                .collect(Collectors.toList());
        if (list.isEmpty()) {
            return null;
        }
        int range = list.size() / (errorCount + 1);
        int index = new Random().nextInt(range);
        logger.printf(Level.DEBUG, "获取代理: range: %d, index: %d", range, index);
        return list.get(index);
    }

    private boolean isTimeout(Date date) {
        return date == null || date.getTime() + 60000 < System.currentTimeMillis();
    }

    public Set<ProxyHost> getProxys() {
        return proxys;
    }

    public Set<ProxyHost> getErrors() {
        return errors;
    }
}
