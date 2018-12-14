package com.tun.app.iats_app.protocol;

import com.tun.app.iats_app.protocol.tagvalue.Field;
import com.tun.app.iats_app.protocol.tagvalue.Tag;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：TanTun
 * 时间：2017/10/18
 * 邮箱：32965926@qq.com
 * 描述：协议命令字枚举
 */

public class Frame {
    private Cmd cmd;
    private Map<Tag, Field> fieldArray;
    private int index;

    public Cmd getCmd() {
        return cmd;
    }

    public void setCmd(Cmd cmd) {
        this.cmd = cmd;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Map<Tag, Field> getFieldArray() {
        return fieldArray;
    }

    private Frame(Cmd cmd) {
        this.cmd = cmd;
        this.fieldArray = new HashMap<>();
    }

    public boolean hasField(Tag tag) {
        return fieldArray.containsKey(tag);
    }

    public Field getField(Tag tag) {
        if (fieldArray.containsKey(tag)) {
            return fieldArray.get(tag);
        } else {
            return null;
        }
    }

    public Frame(Cmd cmd, Map<Tag, Field> fieldArray) {
        this.cmd = cmd;
        this.fieldArray = fieldArray;
    }

    public static Frame parsing(byte[] data) {
        Cmd cmd = Cmd.getCmd(data[2]);

        Frame frame = new Frame(cmd);

        int index = 5;

        while (index < (data[4] & 0x0FF) + 5)
        {
            Tag tag = Tag.getTag(data[index]);
            index = index + 1;

            int len = data[index] & 0x0FF;
            index = index + 1;

            byte[] value = new byte[len];
            System.arraycopy(data, index, value, 0, len);
            index = index + len;

            if (len != 0)
            {
                assert tag != null;
                Object obj = tag.byteArrayToValue(value);
                Field field = Field.getInstance(tag, obj);
                if (field != null) frame.fieldArray.put(tag, field);
            }
        }

        return frame;
    }

    public byte[] toArray() {

        byte[] values = getFieldBytes();
        int check = 0x00;

        for (byte b : values
                ) {
            check = (b & 0x0FF) ^ check;
        }

        ByteBuffer array = ByteBuffer.allocate(values.length + 6);
        array.put(Defines.TP_PACKET_LEAD);
        array.put(Defines.TP_PACKET_START);
        array.put(cmd.getValue());
        array.put((byte)index);
        array.put((byte)values.length);
        array.put(values);
        array.put((byte)check);

        return array.array();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder(500);
        sb.append(cmd.toString()).append("\n");
        sb.append("{").append("\n");
        for (Map.Entry<Tag, Field> f: fieldArray.entrySet()
             ) {
            sb.append("    ").append(f.getValue().toString()).append("\n");
        }
        sb.append("}");

        return  sb.toString();
    }

    private byte[] getFieldBytes() {

        if (fieldArray == null || fieldArray.size() == 0) return new byte[0];

        List<ByteBuffer> list = new ArrayList<>();
        int length = 0;

        for (Map.Entry<Tag, Field> entry : fieldArray.entrySet()
             ) {
            byte tag = entry.getKey().getValue();
            byte len = entry.getKey().getLength();
            //byte[] value = entry.getKey().valueToByteArray(entry.getValue().getValue());
            byte[] value = entry.getValue().getBytes();
            assert value != null;
            length = length + value.length + 2;
            ByteBuffer item = ByteBuffer.allocate(value.length + 2);

            item.put(tag);
            item.put(len);

            for (byte b : value
                ){
                item.put(b);
            }

            list.add(item);
        }

        ByteBuffer buffer = ByteBuffer.allocate(length);
        for (ByteBuffer item : list
             ) {
            buffer.put(item.array());
        }

        return buffer.array();
    }
}
