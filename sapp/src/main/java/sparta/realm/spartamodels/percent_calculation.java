package sparta.realm.spartamodels;

/**
 * Created by Thompsons on 01-Feb-17.
 */

public class percent_calculation {
   public String total,payed,balance,per_payed,per_balance;
    public percent_calculation(String total, String balance)
    {
        this.total=total;
        this.balance=balance;
        this.payed=(Double.parseDouble(total)- Double.parseDouble(balance))+"";

        per_balance=(int)((Double.parseDouble(balance)/ Double.parseDouble(total))*100)+"";
        per_payed=(int)((Double.parseDouble(payed)/ Double.parseDouble(total))*100)+"";
    }



}
