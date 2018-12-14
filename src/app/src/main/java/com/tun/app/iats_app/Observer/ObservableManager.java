package com.tun.app.iats_app.Observer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 作者：TanTun
 * 时间：2018/7/21
 * 邮箱：32965926@qq.com
 * 描述：
 */

public class ObservableManager
        implements ObservableInterface<Function, Object> {

    private static final String TAG = "ObservableManager";
    private HashMap<String, Set<Function>> _mapping;
    private final Object _lockObj = new Object();
    private static ObservableManager _instance;

    public ObservableManager() {
        this._mapping = new HashMap<>();
    }

    public static ObservableManager instance() {
        if (_instance == null) _instance = new ObservableManager();
        return _instance;
    }

    @Override
    public void registerObserver(String name, Function observer) {
        synchronized (_lockObj) {
            Set<Function> observers;
            if (!_mapping.containsKey(name)) {
                observers = new HashSet<>();
                _mapping.put(name, observers);
            } else {
                observers = _mapping.get(name);
            }
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(String name) {
        synchronized (_lockObj) {
            _mapping.remove(name);
        }
    }

    @Override
    public void removeObserver(Function observer) {
        synchronized (_lockObj) {
            for (String key : _mapping.keySet()) {
                Set<Function> observers = _mapping.get(key);
                observers.remove(observer);
            }
        }
    }

    @Override
    public void removeObserver(String name, Function observer) {
        synchronized (_lockObj) {
            if (_mapping.containsKey(name)) {
                Set<Function> observers = _mapping.get(name);
                observers.remove(observer);
            }
        }
    }

    @Override
    public Set<Function> getObserver(String name) {
        Set<Function> observers = null;
        synchronized (_lockObj) {
            if (_mapping.containsKey(name)) {
                observers = _mapping.get(name);
            }
        }
        return observers;
    }

    @Override
    public void clear() {
        synchronized (_lockObj) {
            _mapping.clear();
        }
    }

    public void notify(String name, Object... param) {
        synchronized (_lockObj) {
            if (_mapping.containsKey(name)) {
                Set<Function> observers = _mapping.get(name);
                for (Function o : observers) {
                    o.function(param);
                }
            }
        }
    }
}
