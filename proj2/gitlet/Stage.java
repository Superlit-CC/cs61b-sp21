package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author superlit
 * @create 2023/3/16 22:21
 */
public class Stage implements Serializable {
    private Map<String, String> pathToBlobID = new HashMap<>();  // fileName -> blobsID

    /** 将blob添加到stage中*/
    public void add(String fileName, String blobID) {
        pathToBlobID.put(fileName, blobID);
    }

    /** 移除blob */
    public void remove(String fileName) {
        pathToBlobID.remove(fileName);
    }

    /** 读取add stage */
    public static Stage readAddStage() {
        return Utils.readObject(Repository.ADD_STAGE, Stage.class);
    }

    /** 读取remove stage */
    public static Stage readRemoveStage() {
        return Utils.readObject(Repository.REMOVE_STAGE, Stage.class);
    }

    /** 将文件内容储存到add stage中 */
    public void saveAddStage() {
        Utils.writeObject(Repository.ADD_STAGE, this);
    }

    /** 将文件内容储存到remove stage中 */
    public void saveRemoveStage() {
        Utils.writeObject(Repository.REMOVE_STAGE, this);
    }

    /** 清空add stage */
    public static void clearAddStage() {
        new Stage().saveAddStage();
    }

    /** 清空remove stage */
    public static void clearRemoveStage() {
        new Stage().saveRemoveStage();
    }

    /** 是否为空 */
    public boolean isEmpty() {
        return pathToBlobID.isEmpty();
    }

    public Map<String, String> getPathToBlobID() {
        return pathToBlobID;
    }

    /** 根据key获取blob ID，没有返回null */
    public String getBlobID(String fileName) {
        return pathToBlobID.get(fileName);
    }

    /** 获取当前commit追踪的所有文件名 */
    public Set<String> getAllFilesSet() {
        return pathToBlobID.keySet();
    }
}
