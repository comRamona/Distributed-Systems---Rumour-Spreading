public class Rumour {
    private String message;
    private Integer destinationId;
    public Rumour(String message, Integer destinationId){
       this.message = message;
       this.destinationId = destinationId;
    }

    public String getMessage(){
        return message;
    }
    public Integer getDestinationId(){
        return destinationId;
    }
}
