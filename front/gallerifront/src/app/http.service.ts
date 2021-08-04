import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { ImageModelList } from "../image";
import { config } from "src/environments/config";
import { Observable } from "rxjs";

const OPTIONS = {
  withCredentials: true,
};

@Injectable({
  providedIn: "root",
})
export class HttpService {
  constructor(private http: HttpClient) {}

  allImageModels(): Observable<ImageModelList> {
    return this.http.get<ImageModelList>(`/image/all`, OPTIONS);
  }

  imageModelsOf(page: number, limit: number): Observable<ImageModelList> {
    return this.http.get<ImageModelList>(
      `/image/page/${page}/limit/${limit}`,
      OPTIONS
    );
  }
}
