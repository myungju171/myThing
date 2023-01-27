//
//  SearchView.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/20.
//
import SwiftUI
import Combine

struct ContentoneView: View {
  @ObservedObject private var viewModel = SearchViewModel(network: NetworkService(configuration: .default))
  var body: some View {
    NavigationView{
      VStack {
        SearchBar(searchLabel: $viewModel.searchTerm,
                  onSearchButtonClicked: viewModel.onSearchTapped)
        .padding(EdgeInsets(top: 10, leading: 0, bottom: 10, trailing: 0))
        List(viewModel.items, id: \.title) { item in
          NavigationLink {
            SearchItemDetailView(model: item)
          } label: {
            HStack {
              AsyncImage(url: URL(string: item.image), content: { image in
                image.resizable()
              }, placeholder: {Color.gray})
              .aspectRatio(contentMode: .fit)
              .frame(width: 100, height: 100)
              VStack(alignment: .leading, spacing: 10) {
                Text(item.title)
                  .fontWeight(.bold)
                HStack(spacing: 0) {
                  Text(item.lprice)
                  Text("원")
                }
              }
              .padding()
            }
          }
        }
      }
    }
  }
}

struct ContentViewone_Previews: PreviewProvider {
  static var previews: some View {
    ContentoneView()
  }
}
