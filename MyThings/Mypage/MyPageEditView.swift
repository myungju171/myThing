//
//  MyPageView.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/15.
//

import SwiftUI
import PhotosUI
struct ImagePicker: UIViewControllerRepresentable {
  @Environment(\.presentationMode)
  private var presentationMode
  
  let sourceType: UIImagePickerController.SourceType
  let onImagePicked: (UIImage) -> Void
  
  final class Coordinator: NSObject,
                           UINavigationControllerDelegate,
                           UIImagePickerControllerDelegate {
    @Binding
    private var presentationMode: PresentationMode
    private let sourceType: UIImagePickerController.SourceType
    private let onImagePicked: (UIImage) -> Void
    
    init(presentationMode: Binding<PresentationMode>,
         sourceType: UIImagePickerController.SourceType,
         onImagePicked: @escaping (UIImage) -> Void) {
      _presentationMode = presentationMode
      self.sourceType = sourceType
      self.onImagePicked = onImagePicked
    }
    
    func imagePickerController(_ picker: UIImagePickerController,
                               didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
      let uiImage = info[UIImagePickerController.InfoKey.originalImage] as! UIImage
      onImagePicked(uiImage)
      presentationMode.dismiss()
      
    }
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
      presentationMode.dismiss()
    }
  }
  func makeCoordinator() -> Coordinator {
    return Coordinator(presentationMode: presentationMode,
                       sourceType: sourceType,
                       onImagePicked: onImagePicked)
  }
  
  func makeUIViewController(context: UIViewControllerRepresentableContext<ImagePicker>) -> UIImagePickerController {
    let picker = UIImagePickerController()
    picker.sourceType = sourceType
    picker.delegate = context.coordinator
    return picker
  }
  func updateUIViewController(_ uiViewController: UIImagePickerController,
                              context: UIViewControllerRepresentableContext<ImagePicker>) {
    
  }
}

struct MyPageEditView: View {
  @State var showImagePicker: Bool = false
  @State var image = Image("deep")
  var body: some View {
    ZStack {
      VStack(spacing: 30) {
        image
          .resizable()
          .frame(width: 200, height: 200)
          .cornerRadius(100)
        Button(action: {
          self.showImagePicker.toggle()
        }) {
          Text("사진 편집")
        }
        Text("닉네임")
        //        TextField("닉네임(한글 2자 이상)", text: <#T##Binding<String>#>)
        HStack {
          Text("생일을 선택해주세요!")
            .foregroundColor(Color(uiColor: .systemGray2))
            .background(Color(uiColor: .secondarySystemBackground))
            .padding()
          //          DatePicker(selection: $birthDate, in: ...Date(), displayedComponents: .date) {
          //          }.padding()
        }
        .background(Color(uiColor: .secondarySystemBackground))
        .cornerRadius(10)
        Text("상태메세지")
        Spacer()
        Button("수정") {
          print("수정내용 저장")
        }
        .frame(minWidth: 350)
        .foregroundColor(.white)
        .background(Color(.systemBlue))
        .cornerRadius(10)
      }
      .sheet(isPresented: $showImagePicker) {
        ImagePicker(sourceType: .photoLibrary) { image in
          self.image = Image(uiImage: image)
        }
      }
    }
  }
}
