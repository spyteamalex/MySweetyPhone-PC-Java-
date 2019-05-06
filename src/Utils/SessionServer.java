package Utils;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

public class SessionServer extends Session{
    Thread onStop;

    public SessionServer(Type type, int initPort, Runnable doOnStopSession) throws IOException {
        onStop = new Thread(doOnStopSession);
        socket = new DatagramSocket();
        this.port = initPort;
        JSONObject message = new JSONObject();
        message.put("port", port);
        message.put("type", type.ordinal());
        byte[] buf2 = String.format("%-30s", message.toJSONString()).getBytes();
        DatagramSocket s = new DatagramSocket();
        s.setBroadcast(true);
        DatagramPacket packet = new DatagramPacket(buf2, buf2.length, Inet4Address.getByName("255.255.255.255"), BroadCastingPort);

        broadcasting = new Timer();
        TimerTask broadcastingTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    s.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        broadcasting.schedule(broadcastingTask, 2000, 2000);


        switch (type) {
            case MOUSE:
                t = new Thread(() -> {
                    try {
                        socket = new DatagramSocket(port);

                        if(onStop != null) onStop.start();
                        Robot r = new Robot();
                        while (true) {
                            Message m;
                            int head = -1;
                            do{
                                byte[] buf = new byte[Message.maxSize];
                                DatagramPacket p = new DatagramPacket(buf, buf.length);
                                socket.receive(p);
                                m = new Message(p.getData());
                                MessageParser.messageMap.put(m.getId(), m);
                                if(head == -1)
                                    head = m.getId();
                            }while (m.getNext() != -1);
                            JSONObject msg = (JSONObject) JSONValue.parse(new String(MessageParser.parse(head)));
                            Point p = MouseInfo.getPointerInfo().getLocation();
                            switch ((String)msg.get("Type")){
                                case "mouseMoved":
                                    r.mouseMove(((Long) msg.get("X")).intValue() + (int)p.getX(), ((Long) msg.get("Y")).intValue() + (int)p.getY());
                                    break;
                                case "mouseReleased":
                                    r.mouseRelease(InputEvent.getMaskForButton(((Long) msg.get("Key")).intValue()));
                                    break;
                                case "mousePressed":
                                    r.mousePress(InputEvent.getMaskForButton(((Long) msg.get("Key")).intValue()));
                                    break;
                                case "mouseWheel":
                                    r.mouseWheel(((Long)msg.get("value")).intValue());
                                    break;
                                case "keyReleased":
                                    r.keyRelease(((Long)msg.get("value")).intValue());
                                    break;
                                case "keyPressed":
                                    r.keyPress(((Long)msg.get("value")).intValue());
                                    break;
                                case "swap":
                                    SessionServer ss = new SessionServer(type,port,()->{});
                                    socket.close();
                                    Session.sessions.add(ss);
                                    Session.sessions.remove(this);
                                    ss.Start();
                                    return;
                                case "finish":
                                    r.keyRelease(KeyEvent.VK_ALT);
                                    Stop();
                                    return;
                            }

                        }
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }

                });
                break;
            default:
                throw new RuntimeException("Неизвестный тип сессии");
        }
    }

    public boolean isServer(){
        return true;
    }
}