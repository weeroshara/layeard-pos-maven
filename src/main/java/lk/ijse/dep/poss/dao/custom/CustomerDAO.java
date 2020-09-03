package lk.ijse.dep.poss.dao.custom;

import lk.ijse.dep.poss.dao.SuperDAO;
import lk.ijse.dep.poss.entity.Customer;

public interface CustomerDAO extends SuperDAO<Customer,String> {
     String getLastCustomerId();

}
