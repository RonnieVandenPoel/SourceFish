package gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class Methodes {
	
	public static JPanel inputPanel(){
		JPanel panel = new JPanel(){
			@Override protected void paintComponent(Graphics grphcs) {
				Graphics2D g2d = (Graphics2D) grphcs;
				Color bgColor = new Color(200, 200, 200, 200);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				GradientPaint gp = new GradientPaint(0, 0,bgColor.brighter(), 0, getHeight(), bgColor.darker());
				g2d.setPaint(gp);
			    g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		panel.setBorder(BorderFactory.createRaisedBevelBorder());
		return panel;
	}
	
	public static void setIcon(JFrame frame){
		frame.setIconImage(getFDImage());
		frame.setResizable(false);
	}
	
	public static void frameShizzle(final JFrame frame){
		setIcon(frame);
		frame.addWindowStateListener(new WindowAdapter(){
			public void windowStateChanged(WindowEvent e){
				if(e.getNewState() == JFrame.MAXIMIZED_BOTH){
					frame.setExtendedState(JFrame.NORMAL);
				}
			}
		});
	}
	public static JPanel setBackGroundPane(JFrame frame,final boolean logo){
		JPanel contentPane = new JPanel() {
			@Override protected void paintComponent(Graphics grphcs) {
				Graphics2D g2d = (Graphics2D) grphcs;
				Color bgColor = new Color(153,204,255);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				GradientPaint gp = new GradientPaint(0, 0,bgColor.brighter(), 0, getHeight(), bgColor.darker());
				g2d.setPaint(gp);
			    g2d.fillRect(0, 0, getWidth(), getHeight());
				if(logo){
					int x = (this.getWidth() - footerImage().getWidth(null)) / 2;
				    int y = (this.getHeight() - footerImage().getHeight(null)) / 2;
					g2d.drawImage(footerImage(), x, y,null);
				}
			}
		};
		contentPane.setBounds(0, 0, frame.getWidth(), frame.getHeight());
		contentPane.setBorder(BorderFactory.createRaisedBevelBorder());
		frameShizzle(frame);
		return contentPane;
	}
	
	protected static Image footerImage(){
		java.net.URL imgURL = Methodes.class.getResource("sourcefish.png");
	    if (imgURL != null) {
	    	return new ImageIcon(imgURL).getImage();
	    } else {
	    	return null;
	    }
	}
	
	protected static Image getFDImage() {
        java.net.URL imgURL = Methodes.class.getResource("rsz_sourcefish.png");
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        } else {
        	return null;
        }
    }
	public static void message(String message) {		
		JOptionPane.showMessageDialog(null, message);			
	}
	
	public static void Disconnect(JFrame[] frame) {
		for (JFrame f : frame) {
			f.dispose();
		}
		Login.main(null);		
	}
	public static void Disconnect(JFrame[] frame, String message) {
		for (JFrame f : frame) {
			f.dispose();
		}
		JOptionPane.showMessageDialog(null, message);
		Login.main(null);		
	}
	
	public static boolean testConnectie() {
		boolean test = false;
		DefaultHttpClient client=new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(
				"http://" + getIp() + "/register/tryConnect");
		
		HttpResponse resp = null;
		try {
			resp = client.execute(getRequest);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			
			e1.printStackTrace();	
			return false;
		}
		//System.out.println(resp);
		
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(
			        new InputStreamReader((resp.getEntity().getContent())));
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String output;
		System.out.println("Output from Server .... \n");
		
		if(resp.getStatusLine().getStatusCode()!=200)
		{
			System.out.println(resp.getStatusLine());
		}
		System.out.println(resp.getStatusLine());
		try {
			while ((output = br.readLine()) != null) {
				try {
					JSONObject message = new JSONObject(output);
					String ok = message.getString("msg");
					if(ok.equals("OK")) {
						test = true;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println(output);
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally{
			client.getConnectionManager().shutdown();		
		   }
		return test;
	}
	
	public static boolean testConnectie(String ip) {
		boolean test = false;
		DefaultHttpClient client=new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(
				"http://" + ip + "/register/tryConnect");
		
		HttpResponse resp = null;
		try {
			resp = client.execute(getRequest);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			
			e1.printStackTrace();	
			return false;
		}
		//System.out.println(resp);
		
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(
			        new InputStreamReader((resp.getEntity().getContent())));
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String output;
		System.out.println("Output from Server .... \n");
		
		if(resp.getStatusLine().getStatusCode()!=200)
		{
			System.out.println(resp.getStatusLine());
		}
		System.out.println(resp.getStatusLine());
		try {
			while ((output = br.readLine()) != null) {
				try {
					JSONObject message = new JSONObject(output);
					String ok = message.getString("msg");
					if(ok.equals("OK")) {
						test = true;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println(output);
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally{
			client.getConnectionManager().shutdown();		
		   }
		return test;
	}
	
	public static String getIp() {
		final String PREF_NAME = "IP";
		
		Preferences prefs = Preferences.userNodeForPackage(Options.class);
		String defaultValue = "projecten3.eu5.org";
		String propertyValue = prefs.get(PREF_NAME, defaultValue);
		String ip = propertyValue;
		return ip;
	}
}
