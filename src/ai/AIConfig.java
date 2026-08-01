package ai;
public class AIConfig{
	private String provider;
	private String model;
	private String apiKey;
	public AIConfig(){
	}
	public AIConfig(String provider,String model,String apiKey){
		this.provider=provider;
		this.model=model;
		this.apiKey=apiKey;
	}
	public String getProvider(){
		return provider;
	}
	public void setProvider(String provider){
        	this.provider = provider;
    	}
	public String getModel(){
        	return model;
    	}	
	public void setModel(String model){
        	this.model = model;
    	}
	public String getApiKey(){
        	return apiKey;
    	}
    	public void setApiKey(String apiKey){
        	this.apiKey = apiKey;
    	}
}
