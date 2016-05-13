package supersix.aoi.vacancy;

public class Listitem {
    private int imageId;
    private int[] res_imageId = new int[5];
    private String text;
    private String title;

    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return title;
    }
    public void setResult(int imageId, int index){
        res_imageId[index] = imageId;
    }
    public int getResult(int index){
        return res_imageId[index];
    }
}
