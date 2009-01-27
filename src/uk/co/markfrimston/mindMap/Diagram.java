package uk.co.markfrimston.mindMap;

import java.util.*;

public class Diagram 
{
	public List<Node> nodes = new ArrayList<Node>();
	public List<Join> joins = new ArrayList<Join>();
	
	public void clear()
	{
		nodes = new ArrayList<Node>();
		joins = new ArrayList<Join>();
	}
}
