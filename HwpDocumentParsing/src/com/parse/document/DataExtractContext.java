package com.parse.document;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

public class DataExtractContext {
    private final Map<String, Object> contextData;

    public DataExtractContext() {
        this.contextData = new HashMap<>();
    }

    /**
     * 키에 해당하는 값을 가져옵니다.
     * 
     * @param key 저장된 데이터의 키
     * @return 키에 해당하는 값 또는 null
     */
    public Object get(String key) {
        return contextData.get(key);
    }

    /**
     * 키와 값을 저장합니다.
     * 
     * @param key   저장할 데이터의 키
     * @param value 저장할 값
     */
    public void put(String key, Object value) {
        contextData.put(key, value);
    }

    /**
     * 키에 해당하는 데이터를 삭제합니다.
     * 
     * @param key 삭제할 데이터의 키
     * @return 삭제된 데이터 또는 null
     */
    public Object remove(String key) {
        return contextData.remove(key);
    }

    /**
     * 모든 데이터를 초기화합니다.
     */
    public void clear() {
        contextData.clear();
    }

    /**
     * 키가 존재하는지 확인합니다.
     * 
     * @param key 확인할 키
     * @return 키가 존재하면 true, 그렇지 않으면 false
     */
    public boolean containsKey(String key) {
        return contextData.containsKey(key);
    }

    /**
     * 현재 저장된 모든 데이터를 반환합니다.
     * 
     * @return 저장된 데이터의 Map
     */
    public Map<String, Object> getAll() {
        return new HashMap<>(contextData);
    }

    /**
     * 저장된 데이터의 수를 반환합니다.
     * 
     * @return 데이터 수
     */
    public int size() {
        return contextData.size();
    }

	public File getFile(IProgressMonitor monitor, String title, String[] paths) {
		// TODO Auto-generated method stub
		return null;
	}
}
