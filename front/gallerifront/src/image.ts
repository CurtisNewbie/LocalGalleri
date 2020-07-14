export interface Image {
    id: number;
    href: string;
}

export interface ImageModel {
    id: number;
    _links: {
        self: {
            href: string;
        }
    };
}

export interface ImageModelList {
    _embedded?: {
        imageModelList: ImageModel[];
    }
    _links: {
        page: {
            href: string;
        }
    }
}

export function toImageList(imageModelList: ImageModelList): Image[] {
    let list: Image[] = [];
    imageModelList._embedded.imageModelList.forEach((v, i, arr) => { list.push(toImage(v)) });
    return list;
}

export function toImage(imageModel: ImageModel): Image {
    return { id: imageModel.id, href: imageModel._links.self.href };
}