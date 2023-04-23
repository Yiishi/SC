
public class Transaction {
	private int ID;
	private String state;
	private String user;
	private int value;
	
	
	public Transaction (int ID, String state, String user, int value) {
		this.ID = ID;
		this.state=state;
		this.user=user;
		this.value=value;
	}
	
	public String toString() {
		if(value!=0) {
		return "Transacao "+ID+" : "+state+" pelo valor de "+value+" para o "+user;
		}
		return "Transacao "+ID+" : "+state+" pelo "+user;
	}
	
	
	
}
