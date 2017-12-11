import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class QuadTree {
	 Rectangle spaceMbr;
	 int nodeCapacity;
	 List<Point> elements;
	 boolean hasChild;
	 QuadTree NW, NE, SE, SW; // four subtrees
	 
	public QuadTree(Rectangle mbr,int capacity) {
		spaceMbr = mbr;
		this.nodeCapacity = capacity;
		this.elements =  new ArrayList<Point>();
	}
	

    // Split the tree into 4 quadrants
    private void split(){
        double subWidth =  (this.spaceMbr.getWidth() / 2);
        double subHeight = (this.spaceMbr.getHeight() / 2);
        Point midWidth;
        Point midHeight; 
        midWidth = new Point((this.spaceMbr.x1+subWidth), this.spaceMbr.y1);
        midHeight = new Point(this.spaceMbr.x1,(this.spaceMbr.y1+subHeight));
        
        this.SW = new QuadTree(new Rectangle(this.spaceMbr.x1,this.spaceMbr.y1,midWidth.x,midHeight.y),this.nodeCapacity);
        this.NW = new QuadTree(new Rectangle(midHeight.x,midHeight.y,midWidth.x,this.spaceMbr.y2),this.nodeCapacity);
        this.NE = new QuadTree(new Rectangle(midWidth.x,midHeight.y,this.spaceMbr.x2,this.spaceMbr.y2),this.nodeCapacity);
        this.SE = new QuadTree(new Rectangle(midWidth.x,midWidth.y,this.spaceMbr.x2,midHeight.y),this.nodeCapacity);
    }

    
    /**
     * Insert an object into this tree
     */
    public void insert(Point p){
    	//check if there is chiled or not before insert
    	//First case if node doesn't have child 
    	if(!this.hasChild){
    		/*
    		 * if the elements in the node less than the capacity insert otherwise split the node
    		 * and redistribute the nodes between the children. 
    		*/
    		if(this.elements.size() < this.nodeCapacity){
    			this.elements.add(p);
    		}else{
    			//Number of node exceed the capacity split and then reqrrange the points 
    	    	  this.split();
    	    	  reArrangePointsinChildren(this.elements);
    	    	  this.elements.clear();
    	    	  this.elements = null;
    	    	  this.hasChild = true;
    		}
    	}
    	/*
    	 * Else Case if the node has child we need to trace the place where the point belong to
    	 * */
    	else{
    		if(p.isIntersected(this.SW.spaceMbr)){
    			this.SW.insert(p);
    		}else if(p.isIntersected(this.NW.spaceMbr)){
    			this.NW.insert(p);
    		}else if(p.isIntersected(this.NE.spaceMbr)){
    			this.NE.insert(p);
    		}else if(p.isIntersected(this.SE.spaceMbr)){
    			this.SE.insert(p);
    		}
    	}
      
    }
    
    /**
     * This method redistribute the points between the 4 new quadrant child 
     * @param list
     */
    private void reArrangePointsinChildren(List<Point> list){
    	for(Point p : list){
    		if(p.isIntersected(this.SW.spaceMbr)){
    			this.SW.elements.add(p);
    		}else if(p.isIntersected(this.NW.spaceMbr)){
    			this.NW.elements.add(p);
    		}else if(p.isIntersected(this.NE.spaceMbr)){
    			this.NE.elements.add(p);
    		}else if(p.isIntersected(this.SE.spaceMbr)){
    			this.SE.elements.add(p);
    		}
    	}
    }
    OutputStreamWriter writer;
    private void printLeafNodes(QuadTree node) throws IOException{
    	
    	if(!node.hasChild){
    		System.out.println(node.spaceMbr.toString());
    		result.add(node.spaceMbr);
    		writer.write(toWKT(node.spaceMbr)+"\t"+node.elements.size()+"\n");
    	}else{
    		printLeafNodes(node.SW);
    		printLeafNodes(node.NW);
    		printLeafNodes(node.NE);
    		printLeafNodes(node.SE);
    	}
    }
    
    public String toWKT(Rectangle polygon){
    	return (counter++)+"\tPOLYGON (("
    		    +polygon.x2 + " "+ polygon.y1 
    		    +", "+ polygon.x2 + " "+ polygon.y2
    		    +", "+ polygon.x1 + " "+ polygon.y2
    		    +", "+ polygon.x1 + " "+ polygon.y1
    		    +", "+ polygon.x2 + " "+ polygon.y1
    		    + "))";
    }
    
    List<Rectangle> result; 
    int counter = 0;
	public Rectangle[] packInRectangles(
			final Point[] sample) throws IOException {
		for(Point p : sample){
		  this.insert(p);
		}
		result = new ArrayList<Rectangle>();
		writer = new OutputStreamWriter(
				new FileOutputStream(System.getProperty("user.dir")
						+ "/_quad.WKT", false), "UTF-8");
		printLeafNodes(this);
		writer.close();
		Rectangle[] cellinfo = new Rectangle[result.size()];
		return result.toArray(cellinfo);
	}

	public static void main(String[] args) {

	}

}
