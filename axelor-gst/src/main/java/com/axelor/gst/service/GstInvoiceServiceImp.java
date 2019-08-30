package com.axelor.gst.service;

import com.axelor.apps.account.db.Account;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.AccountManagementServiceAccountImpl;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.account.service.invoice.InvoiceLineService;
import com.axelor.apps.account.service.invoice.InvoiceToolService;
import com.axelor.apps.account.service.invoice.factory.CancelFactory;
import com.axelor.apps.account.service.invoice.factory.ValidateFactory;
import com.axelor.apps.account.service.invoice.factory.VentilateFactory;
import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.repo.AddressRepository;
import com.axelor.apps.base.db.repo.PartnerRepository;
import com.axelor.apps.base.db.repo.ProductRepository;
import com.axelor.apps.base.service.AddressService;
import com.axelor.apps.base.service.PartnerService;
import com.axelor.apps.base.service.alarm.AlarmEngineService;
import com.axelor.apps.businessproject.service.InvoiceServiceProjectImpl;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GstInvoiceServiceImp extends InvoiceServiceProjectImpl implements GstInvoiceService {

  @Inject
  public GstInvoiceServiceImp(
      ValidateFactory validateFactory,
      VentilateFactory ventilateFactory,
      CancelFactory cancelFactory,
      AlarmEngineService<Invoice> alarmEngineService,
      InvoiceRepository invoiceRepo,
      AppAccountService appAccountService,
      PartnerService partnerService,
      InvoiceLineService invoiceLineService) {
    super(
        validateFactory,
        ventilateFactory,
        cancelFactory,
        alarmEngineService,
        invoiceRepo,
        appAccountService,
        partnerService,
        invoiceLineService);
    // TODO Auto-generated constructor stub
  }

  @Inject GstInvoiceLineServiceImp gstInvoiceLineServiceImp;

  @Override
  public Invoice compute(Invoice invoice) throws AxelorException {
    super.compute(invoice);
    BigDecimal netCgst = BigDecimal.ZERO;
    BigDecimal netSgst = BigDecimal.ZERO;
    BigDecimal netIgst = BigDecimal.ZERO;
    for (InvoiceLine invoiceLine : invoice.getInvoiceLineList()) {
      netCgst = netCgst.add(invoiceLine.getCgst());
      netSgst = netSgst.add(invoiceLine.getSgst());
      netIgst = netIgst.add(invoiceLine.getIgst());
    }
    invoice.setNetCgst(netCgst);
    invoice.setNetIgst(netIgst);
    invoice.setNetSgst(netSgst);
    return invoice;
  }

  @Override
  public Invoice setProductItem(Invoice invoice, String idList, int partyId, int addressId) {
    if (idList != null) {
      Partner partner =
          Beans.get(PartnerRepository.class).all().filter("self.id = ?", partyId).fetchOne();
      Address address =
          Beans.get(AddressRepository.class).all().filter("self.id = ?", addressId).fetchOne();
      invoice.setPartner(partner);
      invoice.setAddress(address);
      invoice.setCurrency(partner.getCurrency());

      List<InvoiceLine> invoiceItemList = new ArrayList<InvoiceLine>();
      String[] items =
          idList.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
      long[] results = new long[items.length];
      for (int i = 0; i < items.length; i++) {
        results[i] = Integer.parseInt(items[i]);
        InvoiceLine invoiceLine = new InvoiceLine();
        Product product = Beans.get(ProductRepository.class).find(results[i]);
        try {
          Account account =
              Beans.get(AccountManagementServiceAccountImpl.class)
                  .getProductAccount(
                      product,
                      invoice.getCompany(),
                      invoice.getPartner().getFiscalPosition(),
                      InvoiceToolService.isPurchase(invoice),
                      invoiceLine.getFixedAssets());
          invoiceLine.setAccount(account);

          String fullAddress =
              Beans.get(AddressService.class).computeAddressStr(invoice.getAddress());
          invoice.setAddressStr(fullAddress);
        } catch (AxelorException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        invoiceLine.setProductName("[" + product.getCode() + "] " + product.getName());
        invoiceLine.setPrice(product.getSalePrice());
        invoiceLine.setHsbn(product.getHsbn());
        invoiceLine.setProduct(product);
        invoiceLine.setQty(BigDecimal.ONE);
        try {
          Map<String, Object> gstCalculation = new HashMap<>();
          gstCalculation = gstInvoiceLineServiceImp.setGstOnTaxLine(invoiceLine, invoice);
          BigDecimal exTaxTotal = (BigDecimal) gstCalculation.get("exTaxTotal");
          BigDecimal grossAmount = (BigDecimal) gstCalculation.get("grossAmount");
          BigDecimal igst = (BigDecimal) gstCalculation.get("igst");
          BigDecimal cgst = (BigDecimal) gstCalculation.get("cgst");
          BigDecimal sgst = (BigDecimal) gstCalculation.get("sgst");

          invoiceLine.setExTaxTotal(exTaxTotal);
          invoiceLine.setGrossAmount(grossAmount);
          invoiceLine.setIgst(igst);
          invoiceLine.setSgst(sgst);
          invoiceLine.setCgst(cgst);
        } catch (AxelorException e) {
          e.printStackTrace();
        }
        invoiceItemList.add(invoiceLine);
      }
      invoice.setInvoiceLineList(invoiceItemList);
      try {
        compute(invoice);
      } catch (AxelorException e) {
        e.printStackTrace();
      }
    }

    return invoice;
  }
}
