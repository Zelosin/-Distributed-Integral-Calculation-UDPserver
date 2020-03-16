package zelosin.pack.Services.UDP;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import zelosin.pack.Data.GFGCell;
import zelosin.pack.Services.Serializer;

import java.io.IOException;
import java.net.*;

public class EchoServer extends Thread {

    private static DatagramSocket mWorkingSocket;
    private static Boolean mIsWaitingClients = true;
    private static byte[] mByteBuffer, mReadBuffer;
    private static Integer mClientsCount = 0;
    public static GFGCell mCurrentGFGCell;

    public static ObservableList<SocketAddress> mClientsList = FXCollections.observableArrayList();

    public EchoServer() {
        try {
            mWorkingSocket = new DatagramSocket(4445);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static void stopClientsDetect(){
        try {
            deriveCalculatingByCurrentClients();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static DatagramPacket waitPacket(){
        mReadBuffer = new byte[1024];
        DatagramPacket tReceivingPacket
                = new DatagramPacket(mReadBuffer, mReadBuffer.length);
        try {
            mWorkingSocket.receive(tReceivingPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tReceivingPacket;
    }

    private static void sendPacket(SocketAddress pSocketAddress, GFGUDPProtocol pSendingProtocol){
        mByteBuffer = new byte[1024];
        try {
            mByteBuffer = Serializer.serialize(pSendingProtocol);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DatagramPacket mSendingPacket = new DatagramPacket(mByteBuffer, mByteBuffer.length, pSocketAddress);
        try {
            mWorkingSocket.send(mSendingPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void waitForClients() throws IOException, ClassNotFoundException {
        synchronized (mIsWaitingClients) {
            while (mIsWaitingClients) {
                DatagramPacket mReceivedPacket = waitPacket();
                GFGUDPProtocol tClientReceivedPacket = (GFGUDPProtocol) Serializer.deserialize(mReceivedPacket.getData());
                switch (tClientReceivedPacket.mStatus) {
                    case INIT: {
                        Platform.runLater(
                                () -> {
                                    mClientsList.add(mReceivedPacket.getSocketAddress());
                                }
                        );
                        mClientsCount++;
                        break;
                    }
                    case DISCONNECTION: {
                        Platform.runLater(
                                () -> {
                                    mClientsList.remove(mReceivedPacket.getSocketAddress());
                                }
                        );
                        mClientsCount--;
                        break;
                    }
                    case SUM_RESULT_TASK:{
                        mCurrentGFGCell.plusResult(tClientReceivedPacket.mGFGCell.getResult());
                    }
                }
            }
        }
    }

    private static void deriveCalculatingByCurrentClients() throws IOException, ClassNotFoundException {
        mCurrentGFGCell.setResult((float)0);
        Float tCalculatedDownEdge = Math.min(mCurrentGFGCell.getDownBorder(), mCurrentGFGCell.getUpBorder());
        Float tCalculatedUpEdge = Math.max(mCurrentGFGCell.getDownBorder(), mCurrentGFGCell.getUpBorder());
        Float mThreadValueStep = (tCalculatedUpEdge - tCalculatedDownEdge)/ mClientsCount;
        for (SocketAddress tSocketAddress : mClientsList) {
            sendPacket(tSocketAddress, new GFGUDPProtocol(new GFGCell(tCalculatedDownEdge + mThreadValueStep, tCalculatedDownEdge, mCurrentGFGCell.getAccuracy())
                    , 5, GFGActionType.CALCULATE_TASK));
            tCalculatedDownEdge+= mThreadValueStep;
        }
    }

    public void run() {
        try {
            waitForClients();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}














