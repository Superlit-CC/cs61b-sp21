package gitlet;

import java.io.File;
import java.io.Serializable;

/**
 * @author superlit
 * @create 2023/3/16 22:20
 */
public class Blob implements Serializable {
    private byte[] content;  // 文件内容
    private String blobID;  // 文件内容生成的ID

    /** 根据当前工作目录下的文件名生成blob，如果没有文件，报错 */
    public Blob(String fileName) {
        this.content = Utils.readContents(Utils.join(Repository.CWD, fileName));
        this.blobID = Utils.sha1(this.content);
    }

    /** 根据内容生成blob */
    public Blob(byte[] content) {
        this.content = content;
        this.blobID = Utils.sha1(this.content);
    }

    public byte[] getContent() {
        return content;
    }

    public String getBlobID() {
        return blobID;
    }

    /** 保存当前的blob到blobs里 */
    public void save() {
        Utils.writeObject(Utils.join(Repository.BLOBS, blobID), this);
    }

    /** 保存当前的blob到当前工作目录中 */
    public void saveToCWD(String fileName) {
        // 当前工作目录下的文件
        File file = Utils.join(Repository.CWD, fileName);
        Utils.writeContents(file, content);
    }

    /** 根据给定的blob id去读取blob，如果没有报错 */
    public static Blob readBlob(String blobID) {
        return Utils.readObject(Utils.join(Repository.BLOBS, blobID), Blob.class);
    }
}
