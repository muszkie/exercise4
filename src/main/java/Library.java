import TextMessageReciver.Reciver;

/*
 * This Java source file was auto generated by running 'gradle buildInit --type java-library'
 * by 'muszkie' at '5/10/17 10:54 AM' with Gradle 2.14.1
 *
 * @author muszkie, @date 5/10/17 10:54 AM
 */
public class Library {
    public boolean someLibraryMethod() {
        return true;
        
    }
	public void run(){
		Reciver reciver = new Reciver();
		reciver.recive();
		reciver.closeConnection();
		System.exit(0);
    }
    
}