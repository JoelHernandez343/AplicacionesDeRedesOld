import java.io.Serializable;

    public class Dato implements Serializable{
        int v1;
        float v2;
        String v3;

        public Dato(int v1, float v2, String v3){
            this.v1 = v1;
            this.v2 = v2;//transient para trabajar solo de manera local la variable
            this.v3 = v3;
        }
        int getV1(){
            return this.v1;

        }

        float getV2(){
            return this.v2;
        }

        String getV3(){
            return this.v3;
        }
}
