package com.tun.app.iats_app.protocol.tagvalue;

import com.tun.app.iats_app.utils.ByteUtils;

/**
 * 作者：TanTun
 * 时间：2017/10/20
 * 邮箱：32965926@qq.com
 * 描述：
 */

public final class Field {

    private Object value;
    private String string;
    private byte[] bytes;
    private Tag tag;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        if (value instanceof String)
        {
            this.bytes = tag.stringToValue((String)value);
            this.value = tag.byteArrayToValue(bytes);
            this.string = (String)value;
        }
        else if (value instanceof byte[])
        {
            this.value = tag.byteArrayToValue((byte[])value);
            this.string = tag.format(value);
            this.bytes = (byte[])value;
        }
        else
        {
            this.value = value;
            this.bytes = tag.valueToByteArray(value);
            this.string = tag.format(value);
        }
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public static Field getInstance(Tag tag, Object value)
    {
        Field field = new Field();
        field.setTag(tag);
        field.setValue(value);

        return field;
    }


    public byte[] toArray()
    {
        int count = tag.getLength() + 4;
        byte[] bs = new byte[count];
        bs[0] = (byte) ((tag.getValue() >> 8) & 0xFF);   //TAG 低字节
        bs[1] = (byte) (tag.getValue() & 0xFF);          //TAG 高字节
        bs[2] = (byte) ((tag.getValue() >> 8) & 0xFF);   //TAG 低字节
        bs[3] = (byte) (tag.getValue() & 0xFF);          //TAG 高字节

        return bs;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(100);
        sb.append("[").append(tag.getName()).append("] ");
        sb.append("[T]").append(ByteUtils.byteToHexString(tag.getValue())).append(" ");
        sb.append("[L]").append(tag.getLength()).append(" ");
        sb.append("[V]").append(string);

        return sb.toString();
    }
}
