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
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

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
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

public class PlotWindow extends JFrame {

	private JPanel mainPanel;
	
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
	private  ArrayList<Color> colorList;
	private JFrame f;
	
	private ArrayList<TrialData> trialDataList;
	
	private int numPoints = 0;
	private int numAnchorPoints = 0;
	private int numAnchorlessPoints = 0;
	
	private  double labelWidth;
	private  double labelHeight;
	
	private  ChartPanel chartPanel;
	
	private  char anchorChar = 65;
	private  JFreeChart chart;
	
	private XYTextAnnotation anchorlessTextAnnotation;
	private XYTextAnnotation anchoredTextAnnotation;
	
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
		trialDataList = new ArrayList<TrialData>(10);
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
                return new Dimension(600, 600);
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
		mainPanel = new JPanel();
		mainPanel.setBackground(new Color(255, 255, 255));
		mainPanel.add(chartPanel, BorderLayout.CENTER);
		add(mainPanel, BorderLayout.CENTER);
		
		JComboBox trialSelect = new JComboBox();
		add(trialSelect, BorderLayout.SOUTH);
		
		//add(p, BorderLayout.SOUTH);
		addMenuBar();
		pack();
		setLocationRelativeTo(null);
		
		final double bottomPanelHeight = p.getSize().getHeight();
		
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				Dimension frameSize = f.getContentPane().getSize();
				double minDimension = Math.min(frameSize.getWidth(), frameSize.getHeight()-bottomPanelHeight);
				System.out.println(minDimension);
				resizePanel(minDimension);
			}
		});
		
	}
	
	private void addMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem openFile = new JMenuItem("Open File...");
		openFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openFile();
			}
			
		});
		file.add(openFile);
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
			plotPoints(trialDataList.get(trialDataList.size()-1));
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
		TrialData trial = new TrialData();
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
								coordinate = new Coordinate(x, y, z, timestamp, false);
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
		trialDataList.add(trial);
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
	
    public  void addPoint(Coordinate coordinate) {
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
        	renderer.setSeriesShapesVisible(i, true);
        	//plot.getRenderer().setSeriesShape(i, ShapeUtilities.createDiagonalCross(1, 1));
    	}
    	else {
    		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(0);
    		if (numPoints >= 2) {
    			renderer.removeAnnotation(anchoredTextAnnotation);
    		}
    		anchoredTextAnnotation = new XYTextAnnotation("(" + (double)Math.round(coordinate.getX() * 1000) / 1000 + ", " + (double)Math.round(coordinate.getY() * 1000) / 1000 + ")" , coordinate.getX() + .1, coordinate.getY()+ .3);
    		renderer.addAnnotation(anchoredTextAnnotation);
    		final XYSeries newSeries = new XYSeries(i);
    		newSeries.add(lastCoordinate.getX(), lastCoordinate.getY());
    		newSeries.add(coordinate.getX(), coordinate.getY());
    		plot.getRenderer(0).setSeriesPaint(i, Color.RED);
    		//plot.getRenderer().setSeriesPaint(i+1, Color.RED);	
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
    
    public void addAnchorlessPoint(Coordinate coordinate) {
    	System.out.println("In addAnchorlessPoint");
    	if (numAnchorlessPoints == 0) {
    		System.out.println("Origin point");
    		final XYSeries newSeries = new XYSeries(numAnchorlessPoints);
    		newSeries.add(coordinate.getX(), coordinate.getY());
        	System.out.println("In addPoint. X: " + coordinate.getX() + " Y: " + coordinate.getY());
        	System.out.println("Size of series: " + series.getItemCount());
        	if (anchorlessXYDataset == null) {
        		System.out.println("AnchorlessXYDataset == null");
        		anchorlessXYDataset = new XYSeriesCollection(newSeries);
        		XYLineAndShapeRenderer anchorlessRenderer = new XYLineAndShapeRenderer();
        		anchorlessRenderer.setBaseShapesVisible(false);
        		anchorlessRenderer.setBaseLinesVisible(true);
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
    			renderer.removeAnnotation(anchorlessTextAnnotation);
    		}
    		anchorlessTextAnnotation = new XYTextAnnotation("(" + (double)Math.round(coordinate.getX() * 1000) / 1000 + ", " + (double)Math.round(coordinate.getY() * 1000) / 1000 + ")" , coordinate.getX() + .1, coordinate.getY()+ .3);
    		renderer.addAnnotation(anchorlessTextAnnotation);
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
    	//plot.getRenderer().setSeriesShape(numAnchorlessPoints, new Ellipse2D.Double(-3.5, -3.5, 7, 7));
    	System.out.println("Num Anchorless Points: " + numAnchorlessPoints);
    	renderer.setSeriesShapesVisible(numAnchorlessPoints, false);
    	System.out.println("After setSeriesShapesVisible");
    	//plot.getRenderer().setSeriesShape(i, ShapeUtilities.createDiagonalCross(1, 1));
    	
    	anchorlessLastCoordinate = new Coordinate(coordinate.getX(), coordinate.getY(), coordinate.getZ());
    	numAnchorlessPoints++;
    }
    
    public void plotPoints (TrialData data) {
    	System.out.println("In plotPoints");
    	System.out.println(data.getAnchorPoints().size());
    	
    	for (Coordinate coordinate : data.getAnchorPoints()) {
    		System.out.println("Anchor point");
    		//if (coordinate.getAnchor()) {
    			//setAnchor(coordinate);
    		//} else {
    			addAnchorPoint(coordinate);
    		//}
    	}
    	System.out.println("Add anchorless points: " + data.getAnchorlessPoints().size());
    	for (Coordinate coordinate : data.getAnchorlessPoints()) {
    		System.out.println("Add anchorlessPoint");
    		addAnchorlessPoint(coordinate);
    	}
    	
    	for (Coordinate coordinate : data.getAnchoredPoints()) {
    		addPoint(coordinate);
    	}
    	
    }
    
    public void addAnchorPoint(Coordinate coordinate) {
    	System.out.println("In add anchor point.");
    	if (xyDataset == null) {
    		final XYSeries newSeries = new XYSeries(i);
    		newSeries.add(coordinate.getX(), coordinate.getY());
        	System.out.println("In addPoint. X: " + coordinate.getX() + " Y: " + coordinate.getY());
        	System.out.println("Size of series: " + series.getItemCount());
        	xyDataset = new XYSeriesCollection(newSeries);
	    	//newDataset.addSeries(newSeries);
        	plot.setDataset(0, xyDataset);
    	} else {
	    	final XYSeries newSeries = new XYSeries(i);
	    	newSeries.add(coordinate.getX(), coordinate.getY());
	    	XYSeriesCollection newDataset = (XYSeriesCollection) xyDataset;
	    	newDataset.addSeries(newSeries);
	    	plot.setDataset(0, newDataset);
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
    	renderer.setSeriesShape(i-1, ShapeUtilities.createRegularCross(5, .5f));
    	plot.setRenderer(0, renderer);
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
