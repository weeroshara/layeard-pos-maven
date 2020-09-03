package lk.ijse.dep.poss.dao.custom;

import lk.ijse.dep.poss.dao.SuperDAO;
import lk.ijse.dep.poss.entity.Orders;

public interface OrdersDAO extends SuperDAO<Orders,String> {
     String getLastOrderId();


}
