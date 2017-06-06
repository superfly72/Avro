package com.example;

import com.example.avro.User;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.File;
import java.io.IOException;

/**
 * Created by royh on 30/05/2017.
 */
public class AvroApplication {

    public static void main(String[] args) {
        AvroApplication app = new AvroApplication();
        try {
            File outputFile = new File("users.avro");
            app.serialiseGeneratedClass(outputFile);
            app.deserialiseGeneratedClass(outputFile);

            File schemaFilev1 = new File("src/main/avro/user_v1.avsc");
            File outputFilev1 = new File("user_v1.avro");
            app.serialiseSchema(schemaFilev1, outputFilev1);
            app.deserialiseSchema(schemaFilev1,outputFilev1);


            //File schemaFilev2 = new File("src/main/avro/user_v2.avsc");
            //File outputFilev2 = new File("user_v2.avro");
            //app.serialiseSchema(schemaFilev2, outputFilev2);
            //app.deserialiseSchema(schemaFilev2,outputFilev2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serialiseGeneratedClass (File file) throws IOException {

        System.out.println("serialiseGeneratedClass....");
        // use default favourite colour
        User user1 = new User();
        user1.setName("Bob");
        user1.setFavoriteNumber(256);
        System.out.println(user1);

        // Alternate constructor
        User user2 = new User("Ben", 7, "red");
        System.out.println(user2);

        // Construct via builder
        User user3 = User.newBuilder()
                .setName("Charlie")
                .setFavoriteColor("blue")
                .setFavoriteNumber(null)
                .build();
        System.out.println(user3);

        // Serialize user1, user2 and user3 to disk
        DatumWriter<User> userDatumWriter = new SpecificDatumWriter<>(User.class);
        DataFileWriter<User> dataFileWriter = new DataFileWriter<User>(userDatumWriter);
        dataFileWriter.create(user1.getSchema(), file);
        dataFileWriter.append(user1);
        dataFileWriter.append(user2);
        dataFileWriter.append(user3);
        dataFileWriter.close();
    }

    public void deserialiseGeneratedClass (File file) throws IOException {

        System.out.println("deserialiseGeneratedClass....");
        // Deserialize Users from disk
        DatumReader<User> userDatumReader = new SpecificDatumReader<>(User.class);
        DataFileReader<User> dataFileReader = new DataFileReader<User>(file, userDatumReader);
        User user = null;

        while (dataFileReader.hasNext()) {
            // Reuse user object by passing it to next(). This saves us from
            // allocating and garbage collecting many objects for files with
            // many items.
            user = dataFileReader.next(user);
            System.out.println(user);
        }
    }

    public void serialiseSchema (File schemaFile, File outputFile) throws IOException {

        System.out.println("serialiseSchema....");
        Schema schema = new Schema.Parser().parse(schemaFile);

        //Using this schema, let's create some users.

        GenericRecord user1 = new GenericData.Record(schema);
        user1.put("name", "Alyssa");
        user1.put("favorite_number", 256);
        // Leave favorite color null
        System.out.println(user1);

        GenericRecord user2 = new GenericData.Record(schema);
        user2.put("name", "Ben");
        user2.put("favorite_number", 7);
        user2.put("favorite_color", "red");

        System.out.println(user2);

        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(datumWriter);
        dataFileWriter.create(schema, outputFile);
        dataFileWriter.append(user1);
        dataFileWriter.append(user2);
        dataFileWriter.close();
    }

    public void deserialiseSchema (File schemaFile, File inputFile) throws IOException {

        System.out.println("deserialiseSchema....");
        Schema schema = new Schema.Parser().parse(schemaFile);

        // Deserialize users from disk
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>(schema);
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(inputFile, datumReader);
        GenericRecord user = null;
        while (dataFileReader.hasNext()) {
            // Reuse user object by passing it to next(). This saves us from
            // allocating and garbage collecting many objects for files with
            // many items.
            user = dataFileReader.next(user);
            System.out.println(user);
        }
    }
}