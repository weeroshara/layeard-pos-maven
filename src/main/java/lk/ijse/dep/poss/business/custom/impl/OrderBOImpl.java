package lk.ijse.dep.poss.business.custom.impl;

import lk.ijse.dep.poss.business.custom.OrderBO;
import lk.ijse.dep.poss.dao.DAOFactory;
import lk.ijse.dep.poss.dao.DAOType;
import lk.ijse.dep.poss.dao.custom.ItemDAO;
import lk.ijse.dep.poss.dao.custom.OrderDetailDAO;
import lk.ijse.dep.poss.dao.custom.OrdersDAO;
import lk.ijse.dep.poss.dao.custom.impl.ItemDAOImpl;
import lk.ijse.dep.poss.db.DBConnection;
import lk.ijse.dep.poss.entity.Item;
import lk.ijse.dep.poss.entity.OrderDetail;
import lk.ijse.dep.poss.entity.Orders;
import lk.ijse.dep.poss.util.OrderDetailTM;
import lk.ijse.dep.poss.util.OrderTM;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class OrderBOImpl implements OrderBO { // , Temp

    private OrdersDAO orderDAO = DAOFactory.getInstance().getDAO(DAOType.ORDER);;
    private OrderDetailDAO orderDetailDAO = DAOFactory.getInstance().getDAO(DAOType.ORDER_DETAIL);
    private ItemDAO itemDAO = DAOFactory.getInstance().getDAO(DAOType.ITEM);

    // Interface through injection
/*    @Override
    public void injection() {
        this.orderDAO = DAOFactory.getInstance().getDAO(DAOType.ORDER);
    }  */

    // Setter method injection
/*    private void setOrderDAO(){
        this.orderDAO = DAOFactory.getInstance().getDAO(DAOType.ORDER);
    }*/

    public String getNewOrderId() throws Exception {

        String lastOrderId = orderDAO.getLastOrderId();

        if (lastOrderId == null) {
            return "OD001";
        } else {
            int maxId = Integer.parseInt(lastOrderId.replace("OD", ""));
            maxId = maxId + 1;
            String id = "";
            if (maxId < 10) {
                id = "OD00" + maxId;
            } else if (maxId < 100) {
                id = "OD0" + maxId;
            } else {
                id = "OD" + maxId;
            }
            return id;
        }
    }

    public boolean placeOrder(OrderTM order, List<OrderDetailTM> orderDetails) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
            boolean result = orderDAO.save(new Orders(order.getOrderId(),
                    Date.valueOf(order.getOrderDate()),
                    order.getCustomerId()));
            if (!result) {
                connection.rollback();
                return false;
            }
            for (OrderDetailTM orderDetail : orderDetails) {
                result = orderDetailDAO.save(new OrderDetail(
                        order.getOrderId(), orderDetail.getCode(),
                        orderDetail.getQty(), BigDecimal.valueOf(orderDetail.getUnitPrice())
                ));
                if (!result) {
                    connection.rollback();
                    return false;
                }
                Item item = itemDAO.find(orderDetail.getCode());
                item.setQtyOnHand(item.getQtyOnHand() - orderDetail.getQty());
                result = new ItemDAOImpl().update(item);
                if (!result) {
                    connection.rollback();
                    return false;
                }
            }
            connection.commit();
            return true;
        } catch (Throwable throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
