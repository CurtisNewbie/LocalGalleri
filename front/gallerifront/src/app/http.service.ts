import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { ImageModelList } from "../image";
import { config } from "src/environments/config";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class HttpService {
  constructor(private http: HttpClient) {}

  allImageModels(): Observable<ImageModelList> {
    return this.http.get<ImageModelList>(
      `http://${config.hostname}:${config.port}/image/all`
    );
  }

  imageModelsOf(page: number, limit: number): Observable<ImageModelList> {
    return this.http.get<ImageModelList>(
      `http://${config.hostname}:${config.port}/image/page/${page}/limit/${limit}`
    );
  }
}
