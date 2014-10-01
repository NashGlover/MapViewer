package edu.utk.imaging.mapviewer.view;

import edu.utk.imaging.mapviewer.data.Coordinate;
import edu.utk.imaging.mapviewer.data.TrialData;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

public class PlotWindow extends JFrame {

	private JPanel mainPanel;
	private EastPanel eastPanel;
	
	private  final Random random = new Random();
	private  XYSeries series = new XYSeries("Test");
	private  XYPlot plot;
	
	private  NumberAxis range;
	private  NumberAxis domain;
	
	private  XYDataset xyDataset = null;
	private XYDataset anchorlessXYDataset = null;
	private int i = 0;
	
	private  Coordinate lastCoordinate;
	private Coordinate anchorlessLastCoordinate;

	private Stack<Double> zoomInStack;
	private Stack<Double> zoomOutStack;
	private ArrayList<Color> colorList;
	private JFrame f;
	
	private HashMap<String, TrialData> trialDataList;
	
	private int numPoints = 0;
	private int numAnchorPoints = 0;
	private int numAnchorlessPoints = 0;
	
	private Coordinate lastAnchorPoint = null;
	
	private  double labelWidth;
	private  double labelHeight;
	
	private  ChartPanel chartPanel;
	
	private  char anchorChar = 65;
	private  JFreeChart chart;
	
	private XYTextAnnotation anchorlessTextAnnotation;
	private XYTextAnnotation anchoredTextAnnotation;
	
	private Vector<String> trialNames = null;
	
	private JComboBox trialSelect;
	
	public PlotWindow() {
		initComponents();
	}
	
	public void colorListInit() {
		colorList = new ArrayList<Color>(10);
		colorList.add(Color.BLUE);
		colorList.add(Color.GREEN);
		colorList.add(Color.BLACK);
		colorList.add(Color.CYAN);
		colorList.add(Color.YELLOW);
		colorList.add(Color.MAGENTA);
		colorList.add(Color.ORANGE);
	}
	
	private void initComponents() {
		this.setTitle("MapViewer");
		trialNames = new Vector<String>(10);
		trialDataList = new HashMap<String, TrialData>(10);
		f = this;
		JButton loadFileButton = new JButton("Load File...");
		MouseEvent evt;
		colorListInit();
		zoomInStack = new Stack<Double>();
		zoomOutStack = new Stack<Double>();
		
		XYDataset firstXY = new XYSeriesCollection(series);
		chart = createChart(firstXY);
		plot = (XYPlot) chart.getPlot();
		
		/* Set domain and range */
		range = (NumberAxis) plot.getRangeAxis();
        range.setTickUnit(new NumberTickUnit(1));
        range.setRange(-10, 10);
        domain = (NumberAxis) plot.getDomainAxis();
        domain.setRange(-10, 10);
        domain.setTickUnit(new NumberTickUnit(1));
        
        /* Plot coloring */
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(new Color(150, 150, 150));
        plot.setDomainGridlinePaint(new Color(150, 150, 150));
        plot.setBackgroundPaint(null);
        
		chartPanel = new ChartPanel(chart) {
			@Override
            public Dimension getPreferredSize() {
                return new Dimension(700, 700);
            }
        };
        
        //chartPanel.setMaximumDrawHeight(600);
        
        chartPanel.setPopupMenu(null);
        chartPanel.setMouseZoomable(false);
        GraphMouseListeners listeners = new GraphMouseListeners();
        chartPanel.addMouseMotionListener(listeners);
        chartPanel.addMouseListener(listeners);
        chartPanel.addMouseWheelListener(new MouseWheelListener() {
        	@Override
        	public void mouseWheelMoved(MouseWheelEvent e) {
        		if (e.getWheelRotation() < 0) {
        			zoomIn();
        			System.out.println("Mouse wheel X: " + e.getX());
        		}
        		else {
        			zoomOut();
        		}
        	}
        	
        });
        
        plot.addChangeListener(new PlotChangeListener() {

			@Override
			public void plotChanged(PlotChangeEvent arg0) {
				Rectangle2D plotRectangle = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();
				updateAxis();
			}
        	
        });
        
        loadFileButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent evt) {
        		openFile();
        	}
        });
        
		setLocationRelativeTo(null);
		Rectangle2D plotArea = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();
		JPanel p = new JPanel();
		p.add(loadFileButton);
		//mainPanel = new JPanel();
		//mainPanel.setBackground(new Color(255, 255, 255));
		//mainPanel.add(chartPanel, BorderLayout.CENTER);
		add(chartPanel, BorderLayout.CENTER);
		eastPanel = new EastPanel(this);
		//eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
		add(eastPanel, BorderLayout.EAST);
		
		
		
		trialSelect = new JComboBox();
		add(trialSelect, BorderLayout.SOUTH);
		trialSelect.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					String selectedName = item.toString();
					plotPoints(trialDataList.get(selectedName));
				}
			}
			
		});
		
		//add(p, BorderLayout.SOUTH);
		addMenuBar();
		pack();
		setLocationRelativeTo(null);
		
		final double bottomPanelHeight = p.getSize().getHeight();
		
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				Dimension frameSize = f.getContentPane().getSize();
				Double minDimension = Math.min(frameSize.getWidth()-300, frameSize.getHeight()-bottomPanelHeight);
				System.out.println("MIN SIZE: " + minDimension);
				System.out.println(minDimension);
				resizePanel(minDimension);
				//chartPanel.setPreferredSize(new Dimension(minDimension.intValue(), minDimension.intValue()));
				//pack();
				//validate();
				//repaint();
			}
		});
		
	}
	
	private void addMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem openFile = new JMenuItem("Open File...");
		JMenuItem openFolder = new JMenuItem("Open Folder...");
		
		openFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				openFile();
			}
			
		});
		
		openFolder.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				openFolder();
			}
		});
		file.add(openFile);
		file.add(openFolder);
		menuBar.add(file);
		setJMenuBar(menuBar);
	}
	
	private void openFile() {
		JFileChooser pointFileChooser = new JFileChooser();
		File pointFile = null;
		
		int returnVal = pointFileChooser.showOpenDialog(null);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			pointFile = pointFileChooser.getSelectedFile();
			readFile(pointFile);
			System.out.println("Size of trialDataList = " + trialDataList.size());
			plotPoints(trialDataList.get(pointFile.getName()));
		}
	}
	
	private void openFolder() {
		JFileChooser pointFileChooser = new JFileChooser();
		File pointFolder = null;
		
		//pointFileChooser.setCurrentDirectory(new File("."));
		pointFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		pointFileChooser.setAcceptAllFileFilterUsed(false);
		
		if (pointFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			pointFolder = pointFileChooser.getSelectedFile();
			System.out.println("pointFolder = " + pointFolder.toString());
			File[] listOfFiles = pointFolder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
				String fileName = listOfFiles[i].getName();
				if (listOfFiles[i].isFile() && fileName.substring(fileName.length()-7).equals(".maplog")) {
					System.out.println("File " + fileName);
					System.out.println("Substring = " + fileName.substring(fileName.length()-7));
					readFile(listOfFiles[i]);
				}
			}
		}
	}
	
	public void resizePanel(Double minDimension) {
		Dimension dimension = new Dimension();
		dimension.setSize(minDimension, minDimension);
		chartPanel.setSize(dimension);
		Dimension chartPanelSize = chartPanel.getSize();
		System.out.println("Height: " + chartPanelSize.getHeight());
	}
	
	private void readFile(File pointFile) {
		TrialData trial = new TrialData(pointFile.getName());
		BufferedReader inputReader;
		Scanner lineScanner;
		
		try {
			inputReader = new BufferedReader(new FileReader(pointFile));
			lineScanner = new Scanner(inputReader.readLine());
			String firstLine = lineScanner.next();
			System.out.println(firstLine);
			lineScanner = new Scanner(inputReader.readLine());
			
			/* Anchors */
			while (lineScanner.hasNextDouble()) {
				int i = 0;
				//while (i < 3) {
					Double x = lineScanner.nextDouble();
					Double y = lineScanner.nextDouble();
					Double z = lineScanner.nextDouble();

					Coordinate coordinate = new Coordinate(x, y, z);
					//addAnchorPoint(coordinate);
					trial.addAnchorPoint(coordinate);
					//i++;
				//}
				lineScanner = new Scanner(inputReader.readLine());
				System.out.println("Got a new line");
			}

			lineScanner = new Scanner(inputReader.readLine());
			lineScanner = new Scanner(inputReader.readLine());
			while (lineScanner.hasNextDouble()) {
				System.out.println("There is a double!");
				Double x = lineScanner.nextDouble();
				Double y = lineScanner.nextDouble();
				Double z = lineScanner.nextDouble();
				
				Coordinate coordinate;
				if (lineScanner.hasNextDouble()) {
					Double timestamp = lineScanner.nextDouble();
					coordinate = new Coordinate(x, y, z, timestamp);
				}
				else {
					coordinate = new Coordinate(x, y, z);
				}
				//addAnchorlessPoint(coordinate);
				trial.addAnchorlessPoint(coordinate);
				lineScanner = new Scanner(inputReader.readLine());
			}
			
			lineScanner = new Scanner(inputReader.readLine());
			//lineScanner = new Scanner(inputReader.readLine());
			String currentLine = inputReader.readLine();
			lineScanner = new Scanner(currentLine);
			
			/* Anchored */
			try {
				while (true) {
					System.out.println("Current line: " + currentLine);
					if (lineScanner.hasNextDouble()) {
						System.out.println("There is a double!");
						Double x = lineScanner.nextDouble();
						Double y = lineScanner.nextDouble();
						Double z = lineScanner.nextDouble();
						
						Coordinate coordinate;
						if (lineScanner.hasNextDouble()) {
							Double timestamp = lineScanner.nextDouble();
							coordinate = new Coordinate(x, y, z, timestamp);
						}
						else {
							coordinate = new Coordinate(x, y, z);
						}
						//addPoint(coordinate);
						trial.addAnchoredPoint(coordinate);
					} else {
						System.out.println("There isn't a double.");
						System.out.println("Current line: " + currentLine);
						if (currentLine.equals("AtAnchor")) {
							currentLine = inputReader.readLine();
							lineScanner = new Scanner(currentLine);

							Double x = lineScanner.nextDouble();
							Double y = lineScanner.nextDouble();
							Double z = lineScanner.nextDouble();
							
							Coordinate coordinate;
							if (lineScanner.hasNextDouble()) {
								Double timestamp = lineScanner.nextDouble();
								coordinate = new Coordinate(x, y, z, timestamp, true);
							}
							else {
								coordinate = new Coordinate(x, y, z);
								coordinate.setAnchor();
							}
							
							trial.addAnchoredPoint(coordinate);
						}
					}
					System.out.println("Before the lineScanner");
					currentLine = inputReader.readLine();
					if (currentLine == null) {
						break;
					}
					lineScanner = new Scanner(currentLine);
					System.out.println("After the lineScanner");
				}
			} catch (IOException e) {
				System.out.println("At the end");
			}
			
			
			
			/*while (!currentLine.trim().equals("\n")) {
				System.out.println("Line");*/
				//currentLine = lineScanner.next();
			//}
			
			//System.out.println(firstLine);
		} catch (FileNotFoundException fne) {
		} catch (IOException ioe) {
		}
		trialDataList.put(trial.getName(), trial);
		trialNames.add(trial.getName());
		trialAdded(trial);
	}
	
	public void trialAdded(TrialData trial) {
		trialSelect.addItem(trial.getName());
		trialSelect.setSelectedIndex(trialNames.size()-1);
		eastPanel.addTrial(trial);
	}
	
	public void zoomIn() {
		
		Double domainLength;
		Double domainChangeLength;
		Double rangeLength;
		Double rangeChangeLength;
		
		domainLength = plot.getDomainAxis().getRange().getLength();
		rangeLength = range.getRange().getLength();
		
		if (zoomOutStack.empty()) {
			System.out.println("It's empty!");
			domainChangeLength = domainLength/8;
			rangeChangeLength = rangeLength/8;
			zoomInStack.push(domainChangeLength);
		} else {
			domainChangeLength = zoomOutStack.pop();
			rangeChangeLength = domainChangeLength;
		}
		
		domain.setRange(domain.getLowerBound()+domainChangeLength, domain.getUpperBound()-domainChangeLength);
		range.setRange(range.getLowerBound()+rangeChangeLength, range.getUpperBound()-rangeChangeLength);
		checkTickMarks();
	}
	
	public void zoomOut() {
		Double domainLength = plot.getDomainAxis().getRange().getLength();
		Double domainChangeLength;
		Double rangeLength = range.getRange().getLength();
		Double rangeChangeLength;
		
		if (zoomInStack.empty()) {
			domainChangeLength = domainLength/8;
			rangeChangeLength = rangeLength/8;
			zoomOutStack.push(domainChangeLength);
		} else {
			domainChangeLength = zoomInStack.pop();
			System.out.println("Distance from zoomInStack: " + domainChangeLength);
			rangeChangeLength = domainChangeLength;
		}
		
		domain.setRange(domain.getLowerBound()-domainChangeLength, domain.getUpperBound()+domainChangeLength);
		range.setRange(range.getLowerBound()-rangeChangeLength, range.getUpperBound()+rangeChangeLength);
		checkTickMarks();
	}
	
	public void checkTickMarks() {
		Double axisLength = plot.getDomainAxis().getRange().getLength();
		if (axisLength > 48) {
			if (axisLength < 120) {
				range.setTickUnit(new NumberTickUnit(5));
				domain.setTickUnit(new NumberTickUnit(5));
			} else {
				range.setTickUnit(new NumberTickUnit(10));
				domain.setTickUnit(new NumberTickUnit(10));
			}
		}
		else {
			range.setTickUnit(new NumberTickUnit(1));
			domain.setTickUnit(new NumberTickUnit(1));
		}
	}
	
	public void updateAxis() {
		range = (NumberAxis) plot.getRangeAxis();
		domain = (NumberAxis) plot.getDomainAxis();
	}
	
    public  void addPoint(Coordinate coordinate, Boolean last) {
    	// Origin point
    	System.out.println("Coordinate x in addPoint: " + coordinate.getX());
    	System.out.println("In add point");
    	System.out.println(numPoints);
    	if (numPoints == 0) {
    		System.out.println("Origin point");
    		final XYSeries newSeries = new XYSeries(i);
    		newSeries.add(coordinate.getX(), coordinate.getY());
        	System.out.println("In addPoint. X: " + coordinate.getX() + " Y: " + coordinate.getY());
        	System.out.println("Size of series: " + series.getItemCount());
        	if (xyDataset == null) {
        		xyDataset = new XYSeriesCollection(newSeries);
        		plot.setDataset(0, xyDataset);
        	}
        	else{
        		XYSeriesCollection newDataset = (XYSeriesCollection) xyDataset;
        		newDataset.addSeries(newSeries);
        		plot.setDataset(0, newDataset);
        	}
        	XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(0);
        	plot.getRenderer(0).setSeriesPaint(i, Color.BLUE);
        	plot.getRenderer(0).setSeriesShape(i, new Ellipse2D.Double(-3.5, -3.5, 7, 7));
        	renderer.setBaseLinesVisible(false);
    		renderer.setBaseShapesVisible(true);
        	renderer.setSeriesShapesVisible(i, true);
        	//plot.getRenderer().setSeriesShape(i, ShapeUtilities.createDiagonalCross(1, 1));
    	}
    	else {
    		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(0);
    		if (numPoints >= 2) {
    			plot.removeAnnotation(anchoredTextAnnotation);
    		}
    		anchoredTextAnnotation = new XYTextAnnotation("(" + (double)Math.round((coordinate.getX()-lastAnchorPoint.getX()) * 1000) / 1000 + ", " + (double)Math.round((coordinate.getY()-lastAnchorPoint.getY()) * 1000) / 1000 + ") = " + (double)Math.round((getDistance(coordinate, lastAnchorPoint)) * 1000) / 1000 , coordinate.getX() + .1, coordinate.getY()+ .3);
    		//renderer.addAnnotation(anchoredTextAnnotation);
    		plot.addAnnotation(anchoredTextAnnotation);
    		final XYSeries newSeries = new XYSeries(i);
    		newSeries.add(lastCoordinate.getX(), lastCoordinate.getY());
    		newSeries.add(coordinate.getX(), coordinate.getY());
    		plot.getRenderer(0).setSeriesPaint(i, Color.RED);
    		plot.getRenderer(0).setSeriesVisible(i, true);
    		if (!last)
    			plot.getRenderer(0).setSeriesShape(i, ShapeUtilities.createRegularCross(3, .2f));
    		else
    			plot.getRenderer(0).setSeriesShape(i, ShapeUtilities.createRegularCross(10, .5f));

    		XYSeriesCollection newDataset = (XYSeriesCollection) xyDataset;
    		newDataset.addSeries(newSeries);
    		plot.setDataset(0, newDataset);
    	}
    	/*series.add(coordinate.getX(), coordinate.getY());
    	System.out.println("In addPoint. X: " + coordinate.getX() + " Y: " + coordinate.getY());
    	System.out.println("Size of series: " + series.getItemCount());
    	xyDataset = new XYSeriesCollection(series);
    	plot.setDataset(xyDataset);*/
    	lastCoordinate = new Coordinate(coordinate.getX(), coordinate.getY(), coordinate.getZ());
    	numPoints++;
    	i++;
    }
    
    public void addAnchorlessPoint(Coordinate coordinate, Boolean last) {
    	System.out.println("In addAnchorlessPoint");
    	System.out.println("Number of anchorlessPoint: " + numAnchorlessPoints);
    	if (numAnchorlessPoints == 0) {
    		System.out.println("STARTING: Origin point");
    		final XYSeries newSeries = new XYSeries(numAnchorlessPoints);
    		newSeries.add(coordinate.getX(), coordinate.getY());
        	System.out.println("In addPoint. X: " + coordinate.getX() + " Y: " + coordinate.getY());
        	System.out.println("Size of series: " + series.getItemCount());
        	if (anchorlessXYDataset == null) {
        		System.out.println("AnchorlessXYDataset == null");
        		anchorlessXYDataset = new XYSeriesCollection(newSeries);
        		XYLineAndShapeRenderer anchorlessRenderer = new XYLineAndShapeRenderer();
        		anchorlessRenderer.setBaseShapesVisible(true);
        		anchorlessRenderer.setBaseLinesVisible(false);
        		plot.setRenderer(1, anchorlessRenderer);
        		plot.setDataset(1, anchorlessXYDataset);
        	}
        	else{
        		XYSeriesCollection newDataset = (XYSeriesCollection) anchorlessXYDataset;
        		newDataset.addSeries(newSeries);
        		plot.setDataset(1, newDataset);
        	}
        	System.out.println("Anchorless dataset series number " + anchorlessXYDataset.getSeriesCount());
    	}
    	else {
    		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(1);
    		if (numAnchorlessPoints >= 2) {
    			plot.removeAnnotation(anchorlessTextAnnotation);
    		}
    		anchorlessTextAnnotation = new XYTextAnnotation("(" + (double)Math.round((coordinate.getX()-lastAnchorPoint.getX()) * 1000) / 1000 + ", " + (double)Math.round((coordinate.getY()-lastAnchorPoint.getY()) * 1000) / 1000 + ") = " + (double)Math.round((getDistance(coordinate, lastAnchorPoint)) * 1000) / 1000 , coordinate.getX() + .1, coordinate.getY()+ .3);
    		//renderer.addAnnotation(anchorlessTextAnnotation);
    		plot.addAnnotation(anchorlessTextAnnotation);
    		final XYSeries newSeries = new XYSeries(numAnchorlessPoints);
    		newSeries.add(anchorlessLastCoordinate.getX(), anchorlessLastCoordinate.getY());
    		newSeries.add(coordinate.getX(), coordinate.getY());
    		//plot.getRenderer().setSeriesPaint(numAnchorlessPoints, Color.RED);	
    		XYSeriesCollection newDataset = (XYSeriesCollection) anchorlessXYDataset;
    		newDataset.addSeries(newSeries);
    		plot.setDataset(1, newDataset);
    	}
    	
    	XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(1);
    	System.out.println("After the renderer");
    	plot.getRenderer(1).setSeriesPaint(numAnchorlessPoints, Color.BLUE);
    	if (!last)
    		plot.getRenderer(1).setSeriesShape(numAnchorlessPoints, ShapeUtilities.createRegularCross(3, .2f));
    	else
    		plot.getRenderer(1).setSeriesShape(numAnchorlessPoints, ShapeUtilities.createRegularCross(3, .5f));
    	System.out.println("Num Anchorless Points: " + numAnchorlessPoints);
    	//renderer.setSeriesShapesVisible(numAnchorlessPoints, true);
    	System.out.println("After setSeriesShapesVisible");
    	//plot.getRenderer(1).setSeriesShape(i, ShapeUtilities.createDiagonalCross(1, 1));
    	
    	anchorlessLastCoordinate = new Coordinate(coordinate.getX(), coordinate.getY(), coordinate.getZ());
    	numAnchorlessPoints++;
    }
    
    public void plotPoints(String listName) {
    	System.out.println("In the second plotPoints");
    	plotPoints(trialDataList.get(listName));
    }
    
    public void plotPoints(TrialData data) {
    	xyDataset = null;
		anchorlessXYDataset = null;
		numPoints = 0;
		numAnchorlessPoints = 0;
		numAnchorPoints = 0;
		i = 0;
		Double minX = null;
		Double minY = null;
		Double maxX = null;
		Double maxY = null;
		
		Boolean first = true;
		
		XYItemRenderer clearRenderer = new XYLineAndShapeRenderer();
		XYDataset clearDataset = new XYSeriesCollection();
		plot.clearAnnotations();
		//plot.setRenderer(0, clearRenderer);
		//plot.setRenderer(1, clearRenderer);
		plot.setDataset(0, clearDataset);
		plot.setDataset(1, clearDataset);
		
    	System.out.println("In plotPoints");
    	System.out.println(data.getAnchorPoints().size());
    	
    	int i = 0;
    	int size = data.getAnchorPoints().size();
    	
    	for (Coordinate coordinate : data.getAnchorPoints()) {
    		System.out.println("Anchor point");
    			addAnchorPoint(coordinate);
    			lastAnchorPoint = coordinate;
    			if (first) {
    				first = false;
    				minX = coordinate.getX();
    				maxX = coordinate.getX();
    				minY = coordinate.getY();
    				maxY = coordinate.getY();
    			}
    			else {
    				if (coordinate.getX() > maxX) { maxX = coordinate.getX(); }
    				else if (coordinate.getX() < minX) { minX = coordinate.getX(); }
    				if (coordinate.getY() > maxY) { maxY = coordinate.getY(); }
    				else if (coordinate.getY() < minY) { minY = coordinate.getY(); }
    			}
    			i++;
    	}
    	System.out.println("Add anchorless points: " + data.getAnchorlessPoints().size());
    	for (Coordinate coordinate : data.getAnchorlessPoints()) {
    		System.out.println("Add anchorlessPoint");
    		addAnchorlessPoint(coordinate, false);
    		
    		if (first) {
				first = false;
				minX = coordinate.getX();
				maxX = coordinate.getX();
				minY = coordinate.getY();
				maxY = coordinate.getY();
			}
			else {
				if (coordinate.getX() > maxX) { maxX = coordinate.getX(); }
				else if (coordinate.getX() < minX) { minX = coordinate.getX(); }
				if (coordinate.getY() > maxY) { maxY = coordinate.getY(); }
				else if (coordinate.getY() < minY) { minY = coordinate.getY(); }
			}
    		
    	}
    	
    	i = 0;
    	size = data.getAnchoredPoints().size();
    	
    	System.out.println("Size of anchor point: " + size);
    	
    	for (Coordinate coordinate : data.getAnchoredPoints()) {
    		if (coordinate.getAnchor()) {
    			atAnchorPoint(coordinate);
    		}
    		else {
    			System.out.println("i = " + i);
    			if (i == (size-1)) {
    				addPoint(coordinate, true);
    				System.out.println("It's the last point!");
    			}
    			else
    				addPoint(coordinate, false);
    		}
    		
    		if (first) {
				first = false;
				minX = coordinate.getX();
				maxX = coordinate.getX();
				minY = coordinate.getY();
				maxY = coordinate.getY();
			}
			else {
				if (coordinate.getX() > maxX) { maxX = coordinate.getX(); }
				else if (coordinate.getX() < minX) { minX = coordinate.getX(); }
				if (coordinate.getY() > maxY) { maxY = coordinate.getY(); }
				else if (coordinate.getY() < minY) { minY = coordinate.getY(); }
			}
    		i++;
    	}
    	
    	/* 
    	 * All points are on the screen. Now correct the ranges of the axes
    	 * so that they include all points.
    	 */
    	
    	double rangeX = maxX-minX;
    	double rangeY = maxY-minY;
    	
    	if (rangeX > rangeY) {
    		double remainder = (rangeX-rangeY)/2;
    		domain.setRange(minX,maxX);
    		range.setRange(minY-remainder, maxY+remainder);
    	}
    	else {
    		double remainder = (rangeY-rangeX)/2;
    		domain.setRange(minX-remainder, maxX+remainder);
    		range.setRange(minY, maxY);
    	}
    	
    	System.out.println("last Anchor Point: " + lastAnchorPoint.getX());
    	System.out.println("THE MAX X: " + maxX);
    	System.out.println("THE MIN X: " + minX);
    }
    
    public void addAnchorPoint(Coordinate coordinate) {
    	System.out.println("In add anchor point.");
    	System.out.println("numAnchorPoints = " + numAnchorPoints);
    	if (numAnchorPoints == 0) {
    		final XYSeries newSeries = new XYSeries(i);
    		newSeries.add(coordinate.getX(), coordinate.getY());
        	System.out.println("In addPoint. X: " + coordinate.getX() + " Y: " + coordinate.getY());
        	System.out.println("Size of series: " + series.getItemCount());
        	if (xyDataset == null) {
        		xyDataset = new XYSeriesCollection(newSeries);
        	}
        	else{
        		XYSeriesCollection newDataset = (XYSeriesCollection) xyDataset;
        		newDataset.addSeries(newSeries);
        	}
	    	//newDataset.addSeries(newSeries);
        	plot.setDataset(2, xyDataset);
    	} else {
	    	final XYSeries newSeries = new XYSeries(i);
	    	newSeries.add(coordinate.getX(), coordinate.getY());
	    	XYSeriesCollection newDataset = (XYSeriesCollection) xyDataset;
	    	newDataset.addSeries(newSeries);
	    	plot.setDataset(2, newDataset);
    	}
    	i++; 
    	//XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    	XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(0);
    	renderer.setSeriesLinesVisible(i-1, false);
    	renderer.setSeriesShapesVisible(i-1, true);
    	System.out.println("Just before series paint");
    	System.out.println("numAnchorPoints: " + numAnchorPoints);
    	renderer.setSeriesPaint(i-1, colorList.get(numAnchorPoints));
    	System.out.println("After series paint");
    	//renderer.setSeriesShape(i-1, new Ellipse2D.Double(-4, -4, 8, 8));
    	//renderer.setSeriesShape(i-1, ShapeUtilities.rotateShape(ShapeUtilities.createDiagonalCross(100, .1f), 40.055, 0, 0));
    	renderer.setSeriesShape(i-1, ShapeUtilities.createRegularCross(7, .7f));
    	plot.setRenderer(2, renderer);
    	//plot.addAnnotation(new XYTextAnnotation(new Character((char)(anchorChar+(char)numAnchorPoints)).toString(), coordinate.getX()+.5, coordinate.getY()+.5));
    	plot.addAnnotation(new XYTextAnnotation(new Character((char)(anchorChar+(char)numAnchorPoints)).toString(), coordinate.getX()+computePixelWidth(10), -(coordinate.getY()+computePixelHeight(10))));
       	numAnchorPoints++;
    }
    
    public  void atAnchorPoint(Coordinate coordinate) {
    	lastCoordinate = coordinate;
    }
    
	private  XYDataset getDataset(int n) {
		System.out.println("Get data set");
        //final XYSeries series = new XYSeries("Temp (K�)");
        double temperature;
       /* for (int length = 0; length < N; length++) {
            temperature = K + n * random.nextGaussian();
            series.add(length + 1, temperature);
        }*/
        return new XYSeriesCollection(series);
    }
	
    private  JFreeChart createChart(final XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
            null, "X", "Y", dataset,
            PlotOrientation.VERTICAL, false, false, false);
        ChartUtilities.applyCurrentTheme(chart);
        return chart;
    }
	
    private  double computePixelWidth(double width) {
		Rectangle2D plotRectangle = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();
		
		double plotWidth = plotRectangle.getWidth();
		double domainRange = domain.getRange().getLength();
		return (width*domainRange/plotWidth);
	}
	
	private  double computePixelHeight(double height) {
		Rectangle2D plotRectangle = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();
		
		double plotHeight = plotRectangle.getHeight();
		double rangeRange = range.getRange().getLength();
		return (height*rangeRange/plotHeight);
	}
	
	public void changeXAxis (int delta, double width) {
		System.out.println("Width: " + width);
		Double length = domain.getRange().getLength();
		Double ratio = delta/width;
		Double lengthDelta = ratio*length;
		
		domain.setRange(domain.getLowerBound()+lengthDelta, domain.getUpperBound()+lengthDelta);
	}
	
	public void changeYAxis(int delta, double height) {
		Double length = range.getRange().getLength();
		Double ratio = delta/height;
		Double lengthDelta = ratio*length;
		
		range.setRange(range.getLowerBound()-lengthDelta, range.getUpperBound()-lengthDelta);
	}
	
	private double getDistance(Coordinate coordinate1, Coordinate coordinate2) {
		return Math.sqrt(Math.pow(coordinate2.getX() - coordinate1.getX(), 2) + Math.pow(coordinate2.getY() - coordinate1.getY(), 2));
	}
	
	private double roundPoint(double number) {
		return (double)Math.round(number) * 1000 / 1000;
	}
    
private class GraphMouseListeners extends MouseAdapter {
		
		int startX;
    	int startY;
    	
    	int lastX;
    	int lastY;
    	
    	Boolean dragging = false;
    	
    	@Override
    	public void mousePressed(MouseEvent e) {
    		System.out.println("Clicked");
    		startX = e.getX();
    		startY = e.getY();
    	}
    	
        @Override
        public void mouseDragged(MouseEvent e) {
        	System.out.println("Mouse dragged");
        	int currX = e.getX();
        	int currY = e.getY();
        	
        	System.out.println("Start X: " + startX + "CurrX: " + e.getX());
        	
        	System.out.println("startX - currX = " + (startX-e.getX()));
        	
        	//if (!dragging && Math.abs(startX-e.getX()) > 10)
        	if (!dragging && (Math.sqrt(Math.pow(currX-startX, 2) + Math.pow(currY-startY, 2)) > 10))
        	{
        		dragging = true;
        		lastX = startX;
        		lastY = startY;
        	}
        	if (dragging) {
            	super.mouseDragged(e);
            	int mouseX = e.getX();
            	int mouseY = e.getY();
            	System.out.println("New X: " + mouseX + " New Y: " + mouseY);
                Rectangle2D plotRectangle = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();
                if (mouseX >= plotRectangle.getX() && mouseX <= (plotRectangle.getX() + plotRectangle.getWidth())) {
                	System.out.println("Dragging on plot area");
                }
                
                changeXAxis(lastX-currX, plotRectangle.getWidth());
                changeYAxis(lastY-currY, plotRectangle.getHeight());
            }
        	
        	lastX = currX;
        	lastY = currY;
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
        	System.out.println("Released");
        	dragging = false;
        }
	}
	
}
