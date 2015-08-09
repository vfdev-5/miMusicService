package com.vfdev.mimusicservicelib.core;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by vfomin on 5/27/15.
 */
public class TrackInfo implements Serializable {

    public String id;
    public String title;
    public int duration = -1;
    public String tags;
    public String streamUrl;

    public boolean equals(Object t) {
        if (t instanceof TrackInfo) {
            return this.id.compareTo(((TrackInfo) t).id) == 0;
        }
        return false;
    }

    public HashMap<String, String> fullInfo = new HashMap<>();

    public void writeObject(java.io.ObjectOutputStream out) throws IOException {
        // write 'this' to 'out'...
        out.writeUTF(id);
        out.writeUTF(title);
        out.writeInt(duration);
        out.writeUTF(tags);
        out.writeUTF(streamUrl);
        out.writeObject(fullInfo);
        out.flush();
    }

    public void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        // populate the fields of 'this' from the data in 'in'...
        id = in.readUTF();
        title = in.readUTF();
        duration = in.readInt();
        tags = in.readUTF();
        streamUrl = in.readUTF();
        fullInfo = (HashMap<String, String>) in.readObject();
    }

}
