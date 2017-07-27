package multimail;

public class TelToAddress {
	
	private static final int NUM_ADDRESSES = 9;
	
	private static final String[] GATEWAYS = new String[]{"@message.alltel.com",
			"@txt.att.net", "@myboostmobile.com", "@vtext.com",
			"@messaging.sprintpcs.com", "@tmomail.net", 
			"@email.uscc.net", "@vmobl.com", "@text.republicwireless.com"};

	String Tel;
	String Addresses;
	
	public TelToAddress(String number)
	{
		Tel = number;
		Addresses = "";
	}
	
	/**
	 * @param type 0 for copy to clipboard format, 1 for sending.
	 * @return
	 */
	public String transform(int type)
	{
		parse();
		add_gateways(type);
		return Addresses;
	}
	
	private void add_gateways(int type)
	{
		for(int i = 0; i < GATEWAYS.length; i++){
			switch(type){
			case 0:
				Addresses += Tel + GATEWAYS[i] + "; ";
				break;
			case 1:
				Addresses += Tel + GATEWAYS[i] + ",";
				break;
			}
		}
	}
	
	private void parse()
	{
		Tel = Tel.replaceAll("[()-]", "");
		if(Tel.length() > 10){
			Tel = Tel.substring(1);
		}
	}

}
