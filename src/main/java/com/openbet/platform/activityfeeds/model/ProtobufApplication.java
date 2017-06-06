package com.openbet.platform.activityfeeds.model;

import com.googlecode.protobuf.format.JsonFormat;
import com.openbet.platform.activityfeeds.model.bet.BetModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by royh on 6/06/2017.
 */
public class ProtobufApplication {

    public static void main(String[] args) {

        ProtobufApplication app = new ProtobufApplication();
        try {

            // serialise to protobuf
            File outputFile = new File("bet_out");
            app.serialiseGeneratedClass(outputFile);

            // deserialise from protobuf back to bet
            BetModel.Bet bet = app.deserialiseGeneratedClass(outputFile);
            System.out.println(bet);

            // convert from protobuf to JSON
            JsonFormat js = new JsonFormat();
            String json = js.printToString(bet);
            System.out.println(json);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void serialiseGeneratedClass (File file) throws IOException {

        System.out.println("serialiseGeneratedClass....");
        BetModel.Bet bet = BetModel.Bet.newBuilder()
                .setId("123456")
                .setAccountRef("accnt-ref")
                .setBetTypeRef("MLT")
                .build();
        System.out.println(bet);

        FileOutputStream output = new FileOutputStream(file);
        bet.writeTo(output);
        output.close();

    }

    public BetModel.Bet deserialiseGeneratedClass (File file) throws IOException {

        System.out.println("deserialiseGeneratedClass....");
        return BetModel.Bet.parseFrom(new FileInputStream(file));

    }
}
