package lobby;

public class ChatPreProcessor {
	
	public String[] keyNames = new String[]{};
	
	public String process(String s){
		if(s.startsWith("/me ")){
			return "###I#"+s.substring(4);
		}
		String normal = "###N#";
		if(s.startsWith("###I#"))
			normal = "###I#";
		for (int i = 0; i < keyNames.length; i++) {
			int u = s.indexOf("@"+keyNames[i]+" ");
			if(u >= 0){
				s = s.substring(0, u)+"###Bb#"+s.substring(u, u+keyNames[i].length()+2)+
						normal+s.substring(u+keyNames[i].length()+2);
			}
		}
		return s;
	}
}
