package uk.co.markfrimston.mindMap;

public class Join 
{
	public Node start;
	public Node end;
	
	public boolean equals(Object otherJoin)
	{
		if(otherJoin==null) return false;
		if(!otherJoin.getClass().equals(this.getClass())) return false;
		Join j = (Join)otherJoin;
		if(!((j.start==start && j.end==end) 
				|| (j.start==end && j.end==start)))
		{
			return false;
		}
		return true;
	}
}
