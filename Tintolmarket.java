public class Tintolmarket {
    public int wallet(User user){
        user.getWallet();
    }

    public void classify( Wines wine, int stars){
        
    }

    public void talk (String user, String message){

    }

    public String read(){
    	return "";
    }

    public void add(Wines wine, Image image)throws Exception{
        if(database.exists(wine)){
            throw new Exception("erro, vinho já existe");
        }
        database.add(wine, image);
    }
    public void sell(Wines wine, int value, int quantity )throws Exception{
        if(!database.exists(wine)){
            throw new Exception("erro, vinho não existe");
        }
        //ver agora como fazer isto melhor
    }

}
