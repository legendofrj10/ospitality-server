package com.ospitality.server.requestHandlers;

import com.ospitality.server.common;
import com.ospitality.server.HandleRequestThread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class lbtGetTestsHandler extends Thread{
    Socket socket;
    public lbtGetTestsHandler(Socket clientSocket){
        this.socket=clientSocket;
    }

    @Override
    public void run(){
        try {
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream din = new DataInputStream(socket.getInputStream());

            Statement st = common.getCon().createStatement();
            ResultSet rs = st.executeQuery("SELECT patients.name,patients.patient_id,labtests.testName,labtests.assignmentDate,labtests.testDoneOn FROM patients INNER Join labtests ON patients.patient_id=labtests.patient WHERE labtests.assignmentDate LIKE (SELECT CURDATE())");
            boolean t = rs.next();
            dout.writeBoolean(t);
            if(t){
                do{
                    dout.writeUTF(rs.getString(1)+"./"+rs.getString(2)
                            +"./"+rs.getString(3)+"./"+rs.getString(4)
                            +"./"+rs.getString(5));
                }while(rs.next());
                dout.writeUTF("S");
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        new HandleRequestThread(socket).start();
    }
}
