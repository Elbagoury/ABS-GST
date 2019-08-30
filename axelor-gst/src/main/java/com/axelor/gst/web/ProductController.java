package com.axelor.gst.web;

import com.axelor.app.AppSettings;
import com.axelor.apps.ReportFactory;
import com.axelor.apps.report.engine.ReportSettings;
import com.axelor.exception.AxelorException;
import com.axelor.gst.report.IReport;
import com.axelor.i18n.I18n;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductController {

  private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

 
  public void getProductIds(ActionRequest request, ActionResponse response) {
		List<Long> requestIds = (List<Long>) request.getContext().get("_ids");
		if (requestIds == null) {
			response.setError("please select one product");
		} else {
			String idList = requestIds.toString();
			request.getContext().put("idList", idList);
		}
	}
  
  @SuppressWarnings("unchecked")
  public void generateProductIds(ActionRequest request, ActionResponse response)
      throws AxelorException {

	  String ids = null;
	  List<Long> requestIds = (List<Long>) request.getContext().get("_ids");
		if (requestIds == null) {
			response.setError("please select one product");
		} else {
			ids = requestIds.toString();
			ids = ids.substring(1, ids.length() - 1);
		}

    String name = I18n.get("Product Details");

    String fileLink =
        ReportFactory.createReport(IReport.PRODUCT_DETAILS, name + "-${date}")
            .addParam("productId", ids)
            .addParam("Locale", ReportSettings.getPrintingLocale(null))
            .generate()
            .getFileLink();

    logger.debug("Printing " + name);
    response.setView(ActionView.define(name).add("html", fileLink).map());
  }
}
