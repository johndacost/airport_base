/******************************************************************
 * Axel Rieben & Johnny Da Costa
 * Programmation concurrente : laboratoire 3
 * 23 mai 2017
 ******************************************************************/

package airport.com.version2;

import airport.com.Tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AirportFrameBlockingQueue extends JFrame {

    private static final long serialVersionUID = 1L;
    // liste d'avion � chaque endroits
    private List<AvionBlockingQueue> avionOnAirArray;
    private List<AvionBlockingQueue> avionLandingArray;
    private List<AvionBlockingQueue> avionTermArray;
    private List<AvionBlockingQueue> avionTakeOffArray;
    private List<AvionBlockingQueue> avionOnAirLeaveArray;

    // images d'avion
    public ArrayList<JLabel> listTerm;
    public ArrayList<JLabel> listArr;
    public ArrayList<JLabel> listDep;

    public JLabel nbOnAirLabel;
    public JLabel nbLandingLabel;
    public JLabel nbTermLabel;
    public JLabel nbTakeOffLabel;
    public JLabel nbOnAirLeaveLabel;

    public JButton buttonStart;
    public JButton buttonStop;

    private int nbPisteArr;
    private int nbPisteDep;
    private int nbPlace;

    public Boolean isOpen; // condition de test pour l'ouverture et la fermeture
			   // de l'aéroport

    public AirportFrameBlockingQueue(int _nbPisteArr, int _nbPisteDep, int _nbPlace, int _nbAvion) {
	nbPisteArr = _nbPisteArr;
	nbPisteDep = _nbPisteDep;
	nbPlace = _nbPlace;

	isOpen = false;

	avionOnAirArray = new ArrayList<AvionBlockingQueue>();
	avionLandingArray = new ArrayList<AvionBlockingQueue>();
	avionTermArray = new ArrayList<AvionBlockingQueue>();
	avionTakeOffArray = new ArrayList<AvionBlockingQueue>();
	avionOnAirLeaveArray = new ArrayList<AvionBlockingQueue>();

	listArr = new ArrayList<JLabel>();
	listTerm = new ArrayList<JLabel>();
	listDep = new ArrayList<JLabel>();

	JPanel panel = new JPanel(new BorderLayout());

	JPanel airportPanel = new JPanel();
	airportPanel.setLayout(new GridLayout(1, 3));

	ImageIcon imgRoad = new ImageIcon("img/piste.png");

	JPanel landPanel = new JPanel();
	landPanel.setLayout(new GridLayout(2 + (nbPisteArr - 1), 1));
	ImageIcon imgLand = new ImageIcon("img/landing.png");
	nbLandingLabel = new JLabel("nb avion en approche :", JLabel.CENTER);

	for (int i = 1; i <= _nbPisteArr; i++) {
	    JLabel imgLandingLabel = new JLabel("", Tools.scaleImage(imgLand, 50, 50), JLabel.CENTER);
	    imgLandingLabel.setVisible(false);
	    listArr.add(imgLandingLabel);
	    landPanel.add(imgLandingLabel);
	    landPanel.add(new JLabel("", Tools.scaleImage(imgRoad, 50, 50), JLabel.CENTER));
	}

	landPanel.add(new JLabel());
	landPanel.add(nbLandingLabel);
	airportPanel.add(landPanel);

	JPanel airportImgPanel = new JPanel();
	airportImgPanel.setLayout(new GridLayout(3, 1));
	ImageIcon imgAir = new ImageIcon("img/airport.png");
	airportImgPanel.add(new JLabel("", Tools.scaleImage(imgAir, 150, 150), JLabel.CENTER));
	nbTermLabel = new JLabel("nb avion au terminal :", JLabel.CENTER);
	airportImgPanel.add(nbTermLabel);
	airportPanel.add(airportImgPanel);

	JPanel takeOffPanel = new JPanel();
	takeOffPanel.setLayout(new GridLayout(2 + (nbPisteDep - 1), 1));
	ImageIcon imgTakeOff = new ImageIcon("img/takeoff.png");
	nbTakeOffLabel = new JLabel("nb avion au d�part :", JLabel.CENTER);

	for (int i = 1; i <= _nbPisteDep; i++) {
	    JLabel imgTakeOffLabel = new JLabel("", Tools.scaleImage(imgTakeOff, 50, 50), JLabel.CENTER);
	    imgTakeOffLabel.setVisible(false);
	    listDep.add(imgTakeOffLabel);
	    takeOffPanel.add(new JLabel("", Tools.scaleImage(imgRoad, 50, 50), JLabel.CENTER));
	    takeOffPanel.add(imgTakeOffLabel);
	}

	takeOffPanel.add(nbTakeOffLabel);
	airportPanel.add(takeOffPanel);

	panel.add(airportPanel, BorderLayout.CENTER);

	JPanel parkPanel = new JPanel();

	for (int i = 1; i <= _nbPlace; i++) {
	    ImageIcon imgPark = new ImageIcon("img/waiting.png");
	    JLabel imgParkLabel = new JLabel("", Tools.scaleImage(imgPark, 50, 50), JLabel.CENTER);
	    imgParkLabel.setVisible(false);
	    listTerm.add(imgParkLabel);
	    imgParkLabel.setBorder(BorderFactory.createLineBorder(Color.black));
	    parkPanel.add(imgParkLabel);

	}
	panel.add(parkPanel, BorderLayout.SOUTH);

	JPanel onAirPanel = new JPanel();
	onAirPanel.setLayout(new GridLayout(2, 2));
	ImageIcon imgOnAir = new ImageIcon("img/onair.png");
	nbOnAirLabel = new JLabel("nb avion en air (arrive) :", JLabel.CENTER);
	onAirPanel.add(new JLabel("", Tools.scaleImage(imgOnAir, 50, 50), JLabel.CENTER));
	onAirPanel.add(new JLabel("", Tools.scaleImage(imgOnAir, 50, 50), JLabel.CENTER));
	onAirPanel.add(nbOnAirLabel);
	nbOnAirLeaveLabel = new JLabel("nb avion en air (depart) :", JLabel.CENTER);
	onAirPanel.add(nbOnAirLeaveLabel);
	panel.add(onAirPanel, BorderLayout.NORTH);

	JPanel bouton = new JPanel();
	bouton.setLayout(new GridLayout(1, 2));
	JPanel start = new JPanel();
	JPanel stop = new JPanel();

	buttonStart = new JButton("Start");
	start.add(buttonStart);

	buttonStop = new JButton("Stop");
	buttonStop.setEnabled(false);
	stop.add(buttonStop);

	/**
	 * On débloque tous les avions en attente
	 */
	buttonStart.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		synchronized (AirportFrameBlockingQueue.this) {
		    AirportFrameBlockingQueue.this.isOpen = true;
		    AirportFrameBlockingQueue.this.notifyAll(); // on reveille
								// tous les
		    // avions qui "dorment"
		    buttonStart.setEnabled(false);
		    buttonStop.setEnabled(true);
		}

	    }
	});

	/**
	 * Blocage complet de l'aéroport
	 * 
	 * @param e
	 */
	buttonStop.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		synchronized (AirportFrameBlockingQueue.this) {
		    AirportFrameBlockingQueue.this.isOpen = false;
		    buttonStop.setEnabled(false);
		    buttonStart.setEnabled(true);
		}
	    }
	});

	bouton.add(start);
	bouton.add(stop);
	panel.add(bouton, BorderLayout.EAST);

	this.getContentPane().add(panel);
	this.setTitle("Version 2 - Blocking queue");
	this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Gestion des avions qui veulent attérir
     * 
     * @param avion
     * @throws InterruptedException
     */
    public synchronized void arrive(AvionBlockingQueue avion) throws InterruptedException {
	while (!isOpen) {
	    wait();
	}
	avionOnAirArray.add(avion);
	nbOnAirLabel.setText("" + avionOnAirArray.size());
    }

    /**
     * Gestion des avions sur la piste d'attérissage
     * 
     * @param avion
     * @throws InterruptedException
     */
    public synchronized void land(AvionBlockingQueue avion) throws InterruptedException {
	while (!isOpen) {
	    wait();
	}
	avionOnAirArray.remove(avion);
	nbOnAirLabel.setText("" + avionOnAirArray.size());

	avionLandingArray.add(avion);

	nbLandingLabel.setText("" + avionLandingArray.size());

	updateLandingImage();
    }

    /**
     * Gestion du parkings des avions
     * 
     * @param avion
     * @throws InterruptedException
     */
    public synchronized void park(AvionBlockingQueue avion) throws InterruptedException {
	while (!isOpen) {
	    wait();
	}
	avionLandingArray.remove(avion);
	nbLandingLabel.setText("" + avionLandingArray.size());

	avionTermArray.add(avion);
	nbTermLabel.setText("" + avionTermArray.size());

	updateLandingImage();
	updateParkingImage();
    }

    /**
     * Gestion des avions qui décolle
     * 
     * @param avion
     * @throws InterruptedException
     */
    public synchronized void takeOff(AvionBlockingQueue avion) throws InterruptedException {
	while (!isOpen) {
	    wait();
	}
	avionTermArray.remove(avion);
	nbTermLabel.setText("" + avionTermArray.size());

	avionTakeOffArray.add(avion);
	nbTakeOffLabel.setText("" + avionTakeOffArray.size());

	updateParkingImage();
	updateTakeOffImage();
    }

    /**
     * Gestion des avions qui sont en l'aire
     * 
     * @param avion
     * @throws InterruptedException
     */
    public synchronized void depart(AvionBlockingQueue avion) throws InterruptedException {
	while (!isOpen) {
	    wait();
	}
	avionTakeOffArray.remove(avion);
	nbTakeOffLabel.setText("" + avionTakeOffArray.size());

	avionOnAirLeaveArray.add(avion);
	nbOnAirLeaveLabel.setText("" + avionOnAirLeaveArray.size());

	updateTakeOffImage();
    }

    private void updateLandingImage() {
	for (int i = 0; i < nbPisteArr; i++) {
	    if (i < avionLandingArray.size()) {
		listArr.get(i).setVisible(true);
		listArr.get(i).setText(avionLandingArray.get(i).getCode());
	    } else {
		listArr.get(i).setVisible(false);
	    }
	}
    }

    private void updateParkingImage() {
	for (int i = 0; i < nbPlace; i++) {
	    if (i < avionTermArray.size()) {
		listTerm.get(i).setVisible(true);
		listTerm.get(i).setText(avionTermArray.get(i).getCode());
	    } else {
		listTerm.get(i).setVisible(false);
	    }
	}
    }

    private void updateTakeOffImage() {
	for (int i = 0; i < nbPisteDep; i++) {
	    if (i < avionTakeOffArray.size()) {
		listDep.get(i).setVisible(true);
		listDep.get(i).setText(avionTakeOffArray.get(i).getCode());
	    } else {
		listDep.get(i).setVisible(false);
	    }
	}
    }
}
