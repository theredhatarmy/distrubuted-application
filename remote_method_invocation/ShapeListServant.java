import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

public class ShapeListServant implements ShapeList {

    private Map<Integer, WhiteboardCallback> callbacks;

    private int callbackId;

    private Vector<Shape> theList;

    private int version;

    public ShapeListServant() throws RemoteException {
        callbacks = new HashMap<Integer, WhiteboardCallback>();
        callbackId = 0;
        theList = new Vector<Shape>();
        version = 0;
    }

    public Shape newShape(GraphicalObject g) throws RemoteException {
        version++;
        Shape s = new ShapeServant(g, version);
        theList.addElement(s);

        System.out.println("Created Shape Version:" + version);
        //g.print();

        callback();
        return s;
    }

    public void updateShape(GraphicalObject oldG, GraphicalObject newG) throws RemoteException {
        for ( Shape sh : theList ) {
            GraphicalObject obj = sh.getAllState();
            if (obj.uuid.equals(oldG.uuid) && obj.uuid.equals(newG.uuid)) {
                version++;
                System.out.println("Update Shape Version:" + version);
                Shape s = new ShapeServant(newG, version);
                theList.addElement(s);
                theList.removeElement(sh);
                //newG.print();
                callback();
                return;
            }
        }
        System.out.println("Not found");
        return;
    }

    public void deleteShape(GraphicalObject oldG) throws RemoteException {
        for ( Shape sh : theList ) {
            GraphicalObject obj = sh.getAllState();
            if (obj.uuid.equals(oldG.uuid)) {
                System.out.println("Delete Shape " + sh.getVersion());
                theList.removeElement(sh);
                callback();
                return;
            }
        }
        System.out.println("Not found");
        return;
    }

    public Vector<Shape> allShapes() throws RemoteException {
        return theList;
    }

    public int getVersion() throws RemoteException {
        return version;
    }

    public int register(WhiteboardCallback callback) throws RemoteException {
        callbackId++;
        callbacks.put(callbackId, callback);
        System.out.println("callback register " + callbackId);
        return callbackId;
    }

    public void deregister(int id) throws RemoteException {
        System.out.println("callback deregister " + id);
        try {
            callbacks.remove(id);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("register " + id + " not exist");
        }
    }

    private void callback() {
        for (WhiteboardCallback c : callbacks.values()) {
            if (c!=null)
                try {
                    System.out.println("callbacks..");
                    c.callback(version);
                }
                catch (Exception e) {
                }
        }
    }

}
