/*
 *  Copyright (c) 2012 Jan Kotek
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.mapdb;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.zip.CRC32;

/**
 * Provides serialization and deserialization
 *
 * @author Jan Kotek
 */
public interface Serializer<A> {

    /**
     * Serialize the content of an object into a ObjectOutput
     *
     * @param out ObjectOutput to save object into
     * @param value Object to serialize
     */
    public void serialize( DataOutput out, A value)
            throws IOException;


    /**
     * Deserialize the content of an object from a DataInput.
     *
     * @param in to read serialized data from
     * @param available how many bytes are available in DataInput for reading, may be -1 (in streams) or 0 (null).
     * @return deserialized object
     * @throws java.io.IOException
     */
    public A deserialize( DataInput in, int available)
            throws IOException;

    /**
     * Serializes strings using UTF8 encoding.
     * Used mainly for testing.
     * Does not handle null values.
     */
    Serializer<String> STRING_SERIALIZER = new Serializer<String>() {

        @Override
		public void serialize(DataOutput out, String value) throws IOException {
            final byte[] bytes = value.getBytes(Utils.UTF8);
            out.write(bytes);
        }


        @Override
		public String deserialize(DataInput in, int available) throws IOException {
            if(available==-1) throw new IllegalArgumentException("STRING_SERIALIZER does not work with collections.");
            byte[] bytes = new byte[available];
            in.readFully(bytes);
            return new String(bytes, Utils.UTF8);
        }
    };





    /** Serializes Long into 8 bytes, used mainly for testing.
     * Does not handle null values.*/
     
     Serializer<Long> LONG_SERIALIZER = new Serializer<Long>() {
        @Override
        public void serialize(DataOutput out, Long value) throws IOException {
            if(value != null)
                out.writeLong(value);
        }

        @Override
        public Long deserialize(DataInput in, int available) throws IOException {
            if(available==0) return null;
            return in.readLong();
        }
    };

    /** Serializes Integer into 4 bytes, used mainly for testing.
     * Does not handle null values.*/
    
    Serializer<Integer> INTEGER_SERIALIZER = new Serializer<Integer>() {
        @Override
        public void serialize(DataOutput out, Integer value) throws IOException {
            out.writeInt(value);
        }

        @Override
        public Integer deserialize(DataInput in, int available) throws IOException {
            return in.readInt();
        }
    };

    
    Serializer<Boolean> BOOLEAN_SERIALIZER = new Serializer<Boolean>() {
        @Override
        public void serialize(DataOutput out, Boolean value) throws IOException {
            out.writeBoolean(value);
        }

        @Override
        public Boolean deserialize(DataInput in, int available) throws IOException {
            if(available==0) return null;
            return in.readBoolean();
        }
    };

    


    /** always writes zero length data, and always deserializes it as an empty String */
    Serializer<Object> EMPTY_SERIALIZER = new Serializer<Object>() {
        @Override
        public void serialize(DataOutput out, Object value) throws IOException {
            if(value!=Utils.EMPTY_STRING) throw new IllegalArgumentException();
        }

        @Override
        public Object deserialize(DataInput in, int available) throws IOException {
            if(available>0) throw new InternalError();
            return Utils.EMPTY_STRING;
        }
    };

    /** basic serializer for most classes in 'java.lang' and 'java.util' packages*/
    @SuppressWarnings("unchecked")
    Serializer<Object> BASIC_SERIALIZER = new SerializerBase();



    Serializer<byte[] > BYTE_ARRAY_SERIALIZER = new Serializer<byte[]>() {

        @Override
        public void serialize(DataOutput out, byte[] value) throws IOException {
            if(value==null||value.length==0) return;
            out.write(value);
        }

        @Override
        public byte[] deserialize(DataInput in, int available) throws IOException {
            if(available==-1) throw new IllegalArgumentException("BYTE_ARRAY_SERIALIZER does not work with collections.");
            if(available==0) return null;
            byte[] ret = new byte[available];
            in.readFully(ret);
            return ret;
        }
    } ;

}
