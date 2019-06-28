package Utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Timer;

public abstract class Session{
    protected InetAddress address;
    protected DatagramSocket Dsocket;
    protected Socket Ssocket;
    protected int port;
    protected int type;
    protected Thread t;
    protected Timer broadcasting;
    protected DatagramSocket broadcastingSocket;
    static int BroadCastingPort = 9000;

    public static final int
            NONE = 0,
            MOUSE = 1,
            FILEVIEW = 2,
            SMSVIEW = 3;

    public static Stack<Session> sessions;

    static {
        sessions=new Stack<>();
    }

    public Session(){
        sessions.add(this);
    }

    public void Start(){
        t.start();
    }

    public void Stop() throws IOException {
        if(broadcasting!=null) broadcasting.cancel();
        if(t!=null) t.interrupt();
        if(Dsocket!=null && !Dsocket.isClosed()) Dsocket.close();
        if(Ssocket!=null && !Ssocket.isClosed()) Ssocket.close();
        if(broadcastingSocket!=null && !broadcastingSocket.isClosed()) broadcastingSocket.close();
    }

    public int getPort() {
        return port;
    }

    public int getType() {
        return type;
    }

    public InetAddress getAddress() {
        return address;
    }

    public abstract boolean isServer();

    public DatagramSocket getDatagramSocket() {
        return Dsocket;
    }

    public Socket getSocket() {
        return Ssocket;
    }

    public void setSocket(Socket ssocket) {
        Ssocket = ssocket;
    }

    public static String decodeType(int i){
        switch (i){
            default:
            case NONE:
                return "Пусто";
            case MOUSE:
                return "Эмуляция мыши";
            case FILEVIEW:
                return "Просмотр файлов";
            case SMSVIEW:
                return "Просмотр СМС";
        }
    }

    public static int encodeType(String i){
        switch (i){
            default:
            case "Пусто":
                return NONE;
            case "Эмуляция мыши":
                return MOUSE;
            case "Просмотр файлов":
                return FILEVIEW;
            case "Просмотр СМС":
                return SMSVIEW;
        }
    }
}