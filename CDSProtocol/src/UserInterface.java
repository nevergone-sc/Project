
public interface UserInterface {
	public void setTag(String tag);
	public void print(String str, String tag, String threadID);
	public void print(byte[] bytes, String tag, String threadID);
	public void nextStep(String tag, String threadID);
	public void printErr(String str, String threadID);
	public String getInput(String info, String threadID);
	public void start(ProtocolEntity entity);
}
