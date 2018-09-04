package posist;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

class Record{
    private Date timestamp;
    private String data, nodeId, referenceNodeId, genesisreferenceNodeId, HashValue;
    private int nodeNumber;
    private String[] childreferenceNodeId;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        if(nodeId.length()==4)
        {        
            this.nodeId = nodeId;
        }
        else
        {
            System.out.println("NodeID is not 32 bit. ");
        }
        
    }

   
    public String getHashValue() {
        return HashValue;
    }

    public void setHashValue(String HashValue) {
        this.HashValue = HashValue;
    }

    public int getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(int nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

 

}


class Node<T> {
    private List<Node<T>> children = new ArrayList<Node<T>>();
    private Node<T> parent = null;
    private Record data = null;

    public Node(T data) {
        this.data = (Record) data;
    }
    
    public Node(T data, Node<T> parent) {
        this.data = (Record) data;
        this.parent = parent;
    }
public List<Node<T>> getChildren() {
        return children;
    }

    public void setParent(Node<T> parent) {
        parent.addChild(this);
        this.parent = parent;
    }

    public void addChild(T data) {
        Node<T> child = new Node<T>(data);
        child.setParent(this);
        this.children.add(child);
    }

    public void addChild(Node<T> child) {
        child.setParent(this);
        this.children.add(child);
    }

    public Record getData() {
        return this.data;
    }
    
    public void setData(T data) {
        this.data = (Record) data;
    }

    public boolean isRoot() {
        return (this.parent == null);
    }

    public void removeParent() {
        this.parent = null;
    }
}


class JcaTest {
    private Cipher ecipher;
    private Cipher dcipher;
 
    JcaTest(SecretKey key) {
        try {
            ecipher = Cipher.getInstance("AES");
            dcipher = Cipher.getInstance("AES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            System.out.println("Failed in initialization");
        }
    }
 
    public String encrypt(String str) {
        try {
            byte[] utf8 = str.getBytes("UTF-8");
            byte[] enc = ecipher.doFinal(utf8);
             
            return new sun.misc.BASE64Encoder().encode(enc);
        } catch (Exception e) {
            System.out.println("Failed in Encryption");
        }
        return null;
    }
 
    public String decrypt(String str) {
        try {
            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
 
            byte[] utf8 = dcipher.doFinal(dec);
 
            return new String(utf8, "UTF-8");
        } catch (Exception e) {
            System.out.println("Failed in Decryption");
        }
        return null;
    }

}




public class POSist {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        System.out.println("Enter details for New Node");
        Node parent = null;
        addNew();
        
        
        
        
        
        
        
    }
        
        
     static void addNew()   
    {   
      
        Record R = null;
        Scanner sc = new Scanner(System.in);
        Date timestamp = new Date();
        String hashValue;
        int num=0;
        
        System.out.println("Enter Owner id: ");
        int i=sc.nextInt();
        System.out.println("Enter Value: ");
        float v=sc.nextFloat();
        System.out.println("Enter Owner Name: ");
        String name=sc.nextLine();
        hashValue=applySha256(i+v+name);
        System.out.println("Enter Encryption key: ");
        String mykey=sc.nextLine();
        
        SecretKey key = new SecretKeySpec(mykey.getBytes(), "AES");
         JcaTest encrypter = new JcaTest(key);
        String original= i+v+name+hashValue;
        String encrypted = encrypter.encrypt(original);
        R.setData(encrypted);
        R.setNodeNumber(++num);
        
        
        R.setTimestamp(timestamp);
        
        System.out.println("Enter nodeId: ");
        String nodeId=sc.nextLine();
        
        Node node = new Node<>(R);
        
    }
    
    public static String applySha256(String input){		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");	        
			//Applies sha256 to our input, 
			byte[] hash = digest.digest(input.getBytes("UTF-8"));	        
			StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}	
}
