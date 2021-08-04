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

  public allImageModels(): Observable<ImageModelList> {
    return this.http.get<ImageModelList>(`/image/all`, OPTIONS);
  }

  public imageModelsOf(
    page: number,
    limit: number
  ): Observable<ImageModelList> {
    return this.http.get<ImageModelList>(
      `/image/page/${page}/limit/${limit}`,
      OPTIONS
    );
  }

  public nextPage(lastId: number, limit: number): Observable<ImageModelList> {
    return this.http.post<ImageModelList>(
      `/image/page/next`,
      {
        limit: limit,
        lastId: lastId,
      },
      OPTIONS
    );
  }

  public prevPage(lastId: number, limit: number): Observable<ImageModelList> {
    return this.http.post<ImageModelList>(
      `/image/page/prev`,
      {
        limit: limit,
        lastId: lastId,
      },
      OPTIONS
    );
  }
}
