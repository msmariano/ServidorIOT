package br.com.neuverse.principal;

import java.io.IOException;
import java.util.Vector;
import javax.bluetooth.*;
import javax.swing.JLabel;

/**
 * Minimal Device Discovery example.
 */
public class RemoteDeviceDiscovery {

    public Vector<RemoteDevice> devicesDiscovered = new Vector<RemoteDevice>();
    private JLabel label;

    public void main() throws IOException, InterruptedException {

        final Object inquiryCompletedEvent = new Object();
        devicesDiscovered.clear();
        DiscoveryListener listener = new DiscoveryListener() {
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                System.out.println("Device " + btDevice.getBluetoothAddress() + " found");
                devicesDiscovered.addElement(btDevice);
                try {
                    String blueNome = btDevice.getFriendlyName(false);
                    System.out.println("     name " + blueNome);
                    if (blueNome.equals("neuverseBTIot")) {
                        label.setText("neuverseBTIot pronto.");
                    }
                } catch (IOException cantGetDeviceName) {
                }
            }

            public void inquiryCompleted(int discType) {
                System.out.println("Device Inquiry completed!");
                synchronized (inquiryCompletedEvent) {
                    inquiryCompletedEvent.notifyAll();
                }
            }

            public void serviceSearchCompleted(int transID, int respCode) {
            }

            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
            }
        };
        synchronized (inquiryCompletedEvent) {
            boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.LIAC,
                    listener);
            if (started) {
                label.setText("wait for device inquiry to complete...");
                inquiryCompletedEvent.wait();
                System.out.println(devicesDiscovered.size() + " device(s) found");
            }
        }
    }

    public Vector<RemoteDevice> getDevicesDiscovered() {
        return devicesDiscovered;
    }

    public void setDevicesDiscovered(Vector<RemoteDevice> devicesDiscovered) {
        this.devicesDiscovered = devicesDiscovered;
    }

    public JLabel getLabel() {
        return label;
    }

    public void setLabel(JLabel label) {
        this.label = label;
    }

}