package me.roan.bonuspp;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.gson.Gson;

public class BonusPP {

	public static void main(String[] args){
		JPanel form = new JPanel(new BorderLayout());
		JTextField api = new JTextField(30);
		JTextField name = new JTextField(30);
		JLabel lname = new JLabel("Name: ");
		JLabel lapi = new JLabel("API Key: ");
		JPanel pname = new JPanel(new BorderLayout());
		pname.add(lname, BorderLayout.WEST);
		pname.add(name, BorderLayout.EAST);
		JPanel papi = new JPanel(new BorderLayout());
		papi.add(lapi, BorderLayout.WEST);
		papi.add(api, BorderLayout.EAST);
		form.add(papi, BorderLayout.PAGE_START);
		form.add(pname, BorderLayout.PAGE_END);
		JOptionPane.showMessageDialog(null, form);
		String APIKEY = api.getText(); 
		String USER = name.getText();
		String req = getPage("https://osu.ppy.sh/api/get_user?k=" + APIKEY + "&u=" + USER + "&type=string");
		String user = req.substring(1, req.length() - 1);
		String best = "{scores:" + getPage("https://osu.ppy.sh/api/get_user_best?k=" + APIKEY + "&u=" + USER + "&limit=100&type=string") + "}";
		Scores s = new Gson().fromJson(best, Scores.class);
		double scorepp = 0;
		for(int i = 0; i < s.scores.size(); i++){
			scorepp += s.scores.get(i).pp * Math.pow(0.95D, i);
		}
		user = user.split(",\"events\"")[0] + "}";
		System.out.println(user);
		double totalpp = new Gson().fromJson(user, User.class).pp_raw;
		double bonuspp = totalpp - scorepp;
		JLabel msg = new JLabel("<html><b>Name:</b> " + USER + "<br>"
				+ "<b>Bonus PP:</b> " + (int)bonuspp + "<br>"
				+ "<b>Total PP:</b> " + (int)totalpp + "<br>"
				+ "<b>Total PP without bonus:</b> " + (int)scorepp + "<br>"
						+ "<b>Number of ranked scores:</b> " + (int)(Math.log10(-(bonuspp / 416.6667) + 1.0) / Math.log10(0.9994)) + "</html>");
		JOptionPane.showMessageDialog(null, msg);
	}
	
	public static class User{
		double pp_raw;
	}
	
	public static class Scores{
		List<Score> scores;
		
		public static class Score{
			double pp;
		}
	}
	
	private static final String getPage(String url){
		HttpURLConnection con = null;
		
		try{
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(10000);
			
			InputStream is = con.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder();
		    String line;
		    while ((line = rd.readLine()) != null) {
		      response.append(line);
		      response.append('\r');
		    }
		    rd.close();
		    return response.toString();
		}catch(Exception e){
			return null;
		}
	}
}
