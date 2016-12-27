package me.roan.bonuspp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

import com.google.gson.Gson;

import me.roan.bonuspp.BonusPP.Scores.Score;

/**
 * A simple program to calculate the 
 * amount of osu! bonus PP a player has.
 * @author Roan
 */
public class BonusPP {

	/**
	 * @param args It's possible to call the program
	 *        with your osu! API key as command line argument.
	 */
	public static void main(String[] args){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
		}
		JPanel form = new JPanel(new BorderLayout());
		JPanel labels = new JPanel(new GridLayout(3, 0));
		JPanel fields = new JPanel(new GridLayout(3, 0));
		JTextField api = new JTextField(args.length > 0 ? args[0] : null, 30);
		JTextField name = new JTextField(30);
		JComboBox<String> modes = new JComboBox<String>(new String[]{"osu! Standard", "osu! Taiko", "osu! Catch the Beat", "osu! Mania"});
		JLabel lname = new JLabel("User: ");
		JLabel lapi = new JLabel("API Key: ");
		JLabel lmode = new JLabel("Mode: ");
		labels.add(lapi);
		labels.add(lname);
		labels.add(lmode);
		fields.add(api);
		fields.add(name);
		fields.add(modes);
		form.add(labels, BorderLayout.WEST);
		form.add(fields, BorderLayout.CENTER);
		int option = JOptionPane.showOptionDialog(null, form, "Bonus PP", 0, JOptionPane.QUESTION_MESSAGE, null, new String[]{"OK", "Cancel"}, 0);
		if(api.getText().isEmpty() || name.getText().isEmpty() || option == 1){
			System.exit(0);
		}
		
		String MODE = String.valueOf(modes.getSelectedIndex());
		String APIKEY = api.getText(); 
		String USER = name.getText();
		String req = getPage("https://osu.ppy.sh/api/get_user?k=" + APIKEY + "&u=" + USER + "&type=string&m=" + MODE);
		String user = req.substring(1, req.length() - 1).split(",\"events\"")[0] + "}";
		String best = "{scores:" + getPage("https://osu.ppy.sh/api/get_user_best?k=" + APIKEY + "&u=" + USER + "&limit=100&type=string&m=" + MODE) + "}";
		
		Gson gson = new Gson();
		Scores s = gson.fromJson(best, Scores.class);
		double scorepp = calculateScorePP(s);
		double totalpp = gson.fromJson(user, User.class).pp_raw;
		double bonuspp = totalpp - scorepp;
		
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		JPanel msg = new JPanel(new GridLayout(4, 2, 10, 0));
		msg.setBorder(BorderFactory.createTitledBorder(border, "<html><b>" + USER + "</b> (" + modes.getSelectedItem() + ")</html>"));
		
		msg.add(new JLabel("<html><b>Bonus PP:</b></html>"));
		msg.add(new JLabel(String.valueOf((int)bonuspp)));
		
		msg.add(new JLabel("<html><b>Total PP:</b></html>"));
		msg.add(new JLabel(String.valueOf((int)totalpp)));
		
		msg.add(new JLabel("<html><b>Total PP (without bonus):</b></html>"));
		msg.add(new JLabel(String.valueOf((int)scorepp)));
		
		msg.add(new JLabel("<html><b>Number of ranked scores:</b></html>"));
		int ns = ((int)(Math.log10(-(bonuspp / 416.6667D) + 1.0D) / Math.log10(0.9994D)));
		msg.add(new JLabel(String.valueOf((ns == 0 && bonuspp > 0.0D) ? "25397+" : String.valueOf(ns))));
		
		JPanel graph = new Graph(s);
		JPanel graphpanel = new JPanel(new BorderLayout());
		graphpanel.add(graph, BorderLayout.CENTER);
		graphpanel.setBorder(BorderFactory.createTitledBorder(border, "PP score graph"));
		
		JPanel content = new JPanel(new BorderLayout());
		content.add(msg, BorderLayout.PAGE_START);
		content.add(graphpanel, BorderLayout.CENTER);
				
		if(JOptionPane.showOptionDialog(null, content, "Bonus PP", 0, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Close", "Lookup another player"}, 0) == 1){
			main(new String[]{APIKEY});
		}
	}
	
	/**
	 * Calculates the amount of non-bonus PP
	 * a player has. This is done by adding 
	 * together all the scores from the player's
	 * top 100 and by using linear extrapolation
	 * to account for scores that are not part of
	 * the top 100. (Everything is weighted ofcouse)
	 * @param s The list of the player's top 100 scores
	 * @return The amount of non-bonus PP this player has
	 */
	private static final double calculateScorePP(Scores s){
		double scorepp = 0.0D;
		for(int i = 0; i < s.scores.size(); i++){
			scorepp += s.scores.get(i).pp * Math.pow(0.95D, i);
		}
		return scorepp + extraPolatePPRemainder(s);
	}
	
	/**
	 * Calculates the amount of PP a player
	 * has from non-top-100 scores. Especially 
	 * for top player is can be a significant amount.
	 * If the player has less then 100 top scores this
	 * method returns 0.
	 * @param s The list of the player's top scores
	 * @return The amount of PP the player has from non-top-100 scores
	 */
	private static final double extraPolatePPRemainder(Scores s){
		if(s.scores.size() < 100){
			return 0.0D;
		}
		double[] b = calculateLinearRegression(s);
		double n = s.scores.size() + 1;
		double pp = 0.0D;
		while(true){
			double val = (b[0] + b[1] * n) * Math.pow(0.95D, n);
			if(val < 0.0D){
				break;
			}
			pp += val;
			n++;
		}
		return pp;
	}

	/**
	 * Calculates the linear regression equation from
	 * the player's top 100 scores. This equation is used
	 * to extrapolate the player's scores so that non-top-100
	 * scores can be accounted for.<br><br>
	 * <pre>
	 * The following formulas are used:
	 * B1 = Ox,y / Ox^2
	 * B0 = Uy - B1 * Ux
	 * Ox,y = (1/N) * 'sigma(N,i=1)'((Xi - Ux)(Yi - Uy))
	 * Ox^2 = (1/N) * 'sigma(N,i=1)'((Xi - U)^2)
	 * </pre>
	 * @param s The player's top 100 scores
	 * @return The linear regression equation, or more specific
	 *         this method returns <tt><i>b</i>0</tt> and <tt><i>
	 *         b</i>1</tt> these two values can be used to form an
	 *         equation of the following form <tt><i>y</i> = <i>b</i>0 + 
	 *         <i>b</i>1 * <i>x<i></tt> 
	 */
	private static final double[] calculateLinearRegression(Scores s){
		double sumOxy = 0.0D;
		double sumOx2 = 0.0D;
		double avgX = 0.0D;
		double avgY = 0.0D;
		for(Score sc: s.scores){
			avgX++;
			avgY += sc.pp;
		}
		avgX = avgX / s.scores.size();
		avgY = avgY / s.scores.size();
		double n = 0;
		for(Score sc : s.scores){
			sumOxy += (n - avgX) * (sc.pp - avgY);
			sumOx2 += Math.pow(n - avgX, 2.0D);
			n++;
		}
		double Oxy = sumOxy / s.scores.size();
		double Ox2 = sumOx2 / s.scores.size();
		return new double[]{avgY - (Oxy / Ox2) * avgX, Oxy / Ox2};
	}
	
	/**
	 * Custom JPanel to draw graphs on
	 * @author Roan
	 */
	private static class Graph extends JPanel{
		/**
		 * Serial ID
		 */
		private static final long serialVersionUID = -3992422623907422683L;
		/**
		 * A list of points for the graph
		 */
		private Scores scores;
		
		/**
		 * Creates a new Graph object with the given points
		 * @param s
		 */
		private Graph(Scores s){
			scores = s;
		}
		
		/**
		 * Only returns the preferred height
		 * for this component since the width
		 * is controlled by BorderLayout
		 */
		@Override
		public Dimension getPreferredSize(){
			return new Dimension(0, 100);
		}
		
		/**
		 * Paints both the Raw PP graph and
		 * the Weighted PP graph
		 */
		@Override
		public void paintComponent(Graphics g){
			double w = this.getWidth();
			double h = this.getHeight();
			double maxpp = scores.scores.get(0).pp;
			
			double dx = w / scores.scores.size();
			double dy = h / (maxpp + 2);
					
			for(int i = 0; i < scores.scores.size(); i++){
				g.setColor(Color.BLUE);
				g.fillOval((int) (i * dx), (int) (h - (dy * (scores.scores.get(i).pp + 2))), 2, 2);
				g.setColor(Color.GREEN);
				g.fillOval((int) (i * dx), (int) (h - (dy * ((scores.scores.get(i).pp * Math.pow(0.95D, i)) + 2))), 2, 2);
			}
			g.setColor(Color.BLUE);
			g.drawString("Raw PP", (int) ((scores.scores.size() / 2.0D) * dx), (int) (h - (dy * (scores.scores.get((int) (scores.scores.size() / 2.0D)).pp + 2))) - 2);
			g.setColor(Color.GREEN.darker());
			g.drawString("Weighted PP", (int) ((scores.scores.size() / 2.0D) * dx), (int) (h - (dy * ((scores.scores.get((int) (scores.scores.size() / 2.0D)).pp * Math.pow(0.95D, (scores.scores.size() / 2.0D))) + 2))) - 2);
		}
	}
	
	/**
	 * A class that follows the
	 * same structure as the 
	 * JSON representatation for
	 * a user returned by the 
	 * osu!API does
	 * @author Roan
	 */
	public static class User{
		/**
		 * The visible total amount of
		 * PP this user has, so this includes
		 * bonus PP
		 */
		double pp_raw;
	}
	
	/**
	 * A class that follows the
	 * same structure as the 
	 * JSON representatation for
	 * a user's scores returned by the 
	 * osu!API does
	 * @author Roan
	 */
	public static class Scores{
		/**
		 * The top scores of the user
		 */
		List<Score> scores;
		
		/**
		 * A class that follows the
		 * same structure as the 
		 * JSON representatation for
		 * a user's score returned by the 
		 * osu!API does
		 * @author Roan
		 */
		public static class Score{
			/**
			 * The amount of PP this
			 * score is worth
			 */
			double pp;
		}
	}
	
	/**
	 * Used to make API calls. This method
	 * gets the JSON string returned
	 * by API calls
	 * @param url The API call 'url' to make
	 * @return The JSON string returned
	 */
	private static final String getPage(String url){
		try{
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(10000);
			
		    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		    String line = reader.readLine();
		    reader.close();
		    return line;
		}catch(Exception e){
			return null;
		}
	}
}
