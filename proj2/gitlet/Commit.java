package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 *
 * @author superlit
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;

    /* TODO: fill in the rest of this class. */
    private Date date;
    private Map<String, String> pathToBlobID = new HashMap<>();
    private List<Commit> parents = new ArrayList<>();

    public Commit() {
        this.message = "initial commit";
        this.date = new Date(0);
    }

    public Commit(String message, Commit... parents) {
        this.message = message;
        this.date = new Date();
        for (Commit parent : parents) {
            this.parents.add(parent);
        }
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public List<Commit> getParents() {
        return parents;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "message='" + message + '\'' +
                ", date=" + date +
                ", blobID=" + pathToBlobID +
                ", parents=" + parents +
                '}';
    }

    /** 根据file名称获取当前commit对应的blob id，如果没有返回null */
    public String getBlobID(String fileName) {
        return pathToBlobID.get(fileName);
    }

    /** 根据head获取当前commit */
    public static Commit getCurrentCommit() {
        String head = Utils.readContentsAsString(Repository.HEAD);
        String commitName = Utils.readContentsAsString(new File(head));
        return Utils.readObject(Utils.join(Repository.COMMITS, commitName), Commit.class);
    }

    /** 添加blobID */
    public void addBlobs(Map<String, String> addBlobs) {
        pathToBlobID.putAll(addBlobs);
    }

    /** 删除blobsID */
    public void removeBlobs(Map<String, String> removeBlobs) {
        for (Map.Entry<String, String> entry : removeBlobs.entrySet()) {
            pathToBlobID.remove(entry.getKey());
        }
    }

    /** 删除blobs ID */
    public void removeBlobs(String... fileNames) {
        for (String fileName : fileNames) {
            pathToBlobID.remove(fileName);
        }
    }

    /** 保存当前commit并改变当前分支head指向 */
    public void save() {
        // get sha-1 of initial commit and store it in master head.
        String commitID = this.getCommitID();
        // 获取当前分支绝对路径
        String head = Utils.readContentsAsString(Repository.HEAD);
        // 改变当前分支指针指向新的commit
        // .gitlet/refs/heads/xxxxx
        Utils.writeContents(new File(head), commitID);
        // 储存commit
        // .gitlet/objects/commits/xxxxx
        File commitFile = new File(Repository.COMMITS, commitID);
        Utils.writeObject(commitFile, this);
    }

    /** 获取当前commit的ID */
    public String getCommitID() {
        return Utils.sha1(this.toString());
    }

    /** 根据所给格式打印出commit */
    public void displayLog() {
        System.out.println("===");
        System.out.println("commit " + this.getCommitID());
        List<Commit> parents = this.getParents();
        // 判断是否需要输出merge信息
        if (parents.size() > 1) {
            System.out.println("Merge: " +
                    parents.get(0).getCommitID().substring(0, 7) +
                    parents.get(1).getCommitID().substring(0, 7));
        }
        // 输出date信息
        System.out.println("Date: " + getTimestamp());
        // 输出commit信息
        System.out.println(this.getMessage() + "\n");
    }

    /** 获取所需时间格式 */
    private String getTimestamp() {
        // Thu Jan 01 08:00:00 1970 +0800
        DateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    /** 获取当前commit追踪的所有文件名 */
    public Set<String> getAllFilesSet() {
        return pathToBlobID.keySet();
    }

    /** 获取指定commit id的commit，如果没有，返回null */
    public static Commit getCommit(String commitID) {
        for (String c : Utils.plainFilenamesIn(Repository.COMMITS)) {
            if (c.equals(commitID)) {
                return Utils.readObject(Utils.join(Repository.COMMITS, commitID), Commit.class);
            }
        }
        return null;
    }

    /** 根据文件名，获取该commit下的blob。如果没有，返回null */
    public Blob getBlob(String fileName) {
        String blobID = this.getBlobID(fileName);
        // 如果当前commit中没有该blob
        if (blobID == null) {
            return null;
        }
        return Blob.readBlob(blobID);
    }

    /** 获取所有blobs */
    public Collection<String> getAllBlobsID() {
        return pathToBlobID.values();
    }
}
