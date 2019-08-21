package com.axelor.gst.web;

import java.io.File;
import java.util.List;
import com.axelor.app.AppSettings;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class ProductController {
	public void setProductReportValue(ActionRequest request, ActionResponse response) {
		List<Long> requestIds = (List<Long>) request.getContext().get("_ids");
		if (requestIds == null) {
			response.setError("please select one product");
		} else {
			String ids = requestIds.toString();
			ids = ids.substring(1, ids.length() - 1);
			request.getContext().put("ids", ids);
			String idList = requestIds.toString();
			request.getContext().put("idList", idList);
			String attachmentPathProduct = AppSettings.get().getPath("file.upload.dir", "");
			attachmentPathProduct = attachmentPathProduct.endsWith(File.separator) ? attachmentPathProduct
					: attachmentPathProduct + File.separator;
			request.getContext().put("Path", attachmentPathProduct);
		}
	}
}
