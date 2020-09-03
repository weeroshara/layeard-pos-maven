package lk.ijse.dep.poss.dao.custom;

import lk.ijse.dep.poss.dao.SuperDAO;
import lk.ijse.dep.poss.entity.Item;

public interface ItemDAO extends SuperDAO<Item,String> {

     String getLastItemCode();
}
